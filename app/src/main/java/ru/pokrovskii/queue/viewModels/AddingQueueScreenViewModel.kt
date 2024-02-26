package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.QueueApi
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.DTO.QueueDTO
import ru.pokrovskii.queue.domain.DTO.UserDTO
import ru.pokrovskii.queue.domain.model.Queue
import javax.inject.Inject

@HiltViewModel
class AddingQueueScreenViewModel @Inject constructor(
    private val userDAO: UserDAO,
    private val queueApi: QueueApi,
    private val userApi: UserApi,
    private val auth: FirebaseAuth,
) : ViewModel() {

    // Property for name of queue to create
    private val _nameOfNewQueue: MutableStateFlow<String> = MutableStateFlow("")
    val nameOfNewQueue: StateFlow<String> = _nameOfNewQueue

    // Property for name of queue to afd
    private val _nameOfAddQueue: MutableStateFlow<String> = MutableStateFlow("")
    val nameOfAddQueue: StateFlow<String> = _nameOfAddQueue

    private val _resultOfCreatingQueue: MutableStateFlow<ResultOfRequest<Queue>> =
        MutableStateFlow(ResultOfRequest.Loading)
    val resultOfCreateQueue: StateFlow<ResultOfRequest<Queue>> = _resultOfCreatingQueue

    fun updateNameOfNewQueue(nameOfQueue: String) {
        _nameOfNewQueue.update { nameOfQueue }
    }

    fun updateNameOfAddQueue(nameOfQueue: String) {
        _nameOfAddQueue.update { nameOfQueue }
    }

    fun createQueue(switchingAdmins: Boolean) {
        viewModelScope.launch {
            val currUser = userDAO.getCurrUser()
            var newQueue =
                Queue(
                    name = _nameOfNewQueue.value,
                    users = mutableListOf(UserDTO(currUser!!.id, currUser.username, true)),
                    allUsersAreAdmins = switchingAdmins
                )
            val resultOfRequest = queueApi.createQueue(newQueue)
            if (resultOfRequest is ResultOfRequest.Success) {
                newQueue = resultOfRequest.result
                // Adding new queue to DB
                currUser.queues.add(QueueDTO(newQueue.id, newQueue.name, true))
                launch {
                    userApi.updateQueuesOfCurrUser(currUser.queues, auth.currentUser!!.uid)
                }
                // Adding info about user to queue
                launch {
                    userDAO.updateUser(currUser)
                }
            }
            _resultOfCreatingQueue.update { resultOfRequest }
        }
    }

    fun addQueue() {
        viewModelScope.launch {
            val currUser = userDAO.getCurrUser()
            // If user is already have this queue
            if (checkForAlreadyAddedQueue(currUser!!.queues, _nameOfAddQueue.value)) {
                _resultOfCreatingQueue.update { ResultOfRequest.Error("You are already in this queue") }
                return@launch
            }
            val resultOfGettingQueue = queueApi.getQueue(_nameOfAddQueue.value)
            if (resultOfGettingQueue is ResultOfRequest.Success) {
                val newQueue = resultOfGettingQueue.result
                currUser.queues.add(QueueDTO(newQueue.id, newQueue.name, false))
                // Add queue to user
                launch {
                    userApi.updateQueuesOfCurrUser(currUser.queues, auth.currentUser!!.uid)
                }
                // Add user to queue
                launch {
                    queueApi.addUserToQueue(
                        UserDTO(
                            currUser.id,
                            currUser.username,
                            false
                        ), newQueue.id
                    )
                }
                // Update user in local DB
                launch {
                    userDAO.updateUser(currUser)
                }
            }

            _resultOfCreatingQueue.update { resultOfGettingQueue }
        }
    }

    private fun checkForAlreadyAddedQueue(queues: MutableList<QueueDTO>, newId: String): Boolean {
        for (queue in queues) {
            if (queue.id == newId) {
                return true
            }
        }
        return false
    }

}
