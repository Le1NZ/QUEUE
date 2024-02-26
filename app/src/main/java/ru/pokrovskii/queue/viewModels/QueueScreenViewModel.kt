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
class QueueScreenViewModel @Inject constructor(
    private val queueApi: QueueApi,
    private val userApi: UserApi,
    private val userDAO: UserDAO,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _resultOfStarting: MutableStateFlow<ResultOfRequest<Queue>?> =
        MutableStateFlow(null)
    val resultOfStarting: StateFlow<ResultOfRequest<Queue>?> = _resultOfStarting

    private val _resultOfDeleting: MutableStateFlow<ResultOfRequest<Unit>?> = MutableStateFlow(null)
    val resultOfDeleting: StateFlow<ResultOfRequest<Unit>?> = _resultOfDeleting

    private val _resultOfNextOrReturn: MutableStateFlow<ResultOfRequest<Unit>?> =
        MutableStateFlow(null)
    val resultOfNextOfReturn: StateFlow<ResultOfRequest<Unit>?> = _resultOfNextOrReturn

    private val _resultOfKickOut: MutableStateFlow<ResultOfRequest<Unit>?> =
        MutableStateFlow(null)
    val resultOfKickOut: StateFlow<ResultOfRequest<Unit>?> = _resultOfKickOut


    private val _resultOfMakingAdmin: MutableStateFlow<ResultOfRequest<Unit>?> =
        MutableStateFlow(null)
    val resultOfMakingAdmin: StateFlow<ResultOfRequest<Unit>?> = _resultOfMakingAdmin

    private val _isCurrentUserAdmin: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isCurrentUserAdmin: StateFlow<Boolean> = _isCurrentUserAdmin


    fun starting(id: String) {
        viewModelScope.launch {
            _resultOfStarting.update { null }
            queueApi.startListeningQueue(id) { queue: Queue? ->
                _resultOfStarting.value = ResultOfRequest.Success(queue!!)
            }
        }
    }

    fun deleteQueue(currQueue: Queue) {
        viewModelScope.launch {
            val currUser = userDAO.getCurrUser()!!
            var resultOfRequest1: ResultOfRequest<Unit> = ResultOfRequest.Loading
            var resultOfRequest2: ResultOfRequest<Unit> = ResultOfRequest.Loading
            val currQueueDTO = findQueueDTO(currUser.queues, currQueue.id)
            val currUserDTO = UserDTO(currUser.id, currUser.username, currQueueDTO.admin)
            val job1 = launch {
                resultOfRequest1 = userApi.deleteQueueFromUser(currQueueDTO, auth.currentUser!!.uid)
            }
            val job2 = launch {
                resultOfRequest2 = queueApi.deleteUserFromQueue(currUserDTO, currQueue.id)
            }
            launch {
                queueApi.endListeningQueue(currQueue.id)
            }
            launch {
                currUser.queues.remove(currQueueDTO)
                userDAO.updateUser(currUser)
            }

            job1.join()
            job2.join()
            if (resultOfRequest1 is ResultOfRequest.Success && resultOfRequest2 is ResultOfRequest.Success) {
                _resultOfDeleting.update {
                    ResultOfRequest.Success(Unit)
                }
            } else {
                _resultOfDeleting.update {
                    ResultOfRequest.Error("some problems")
                }
            }
        }
    }

    fun theNextUser(id: String) {
        viewModelScope.launch {
            _resultOfNextOrReturn.update { null }

            val resultOfRequest: ResultOfRequest<Unit> = queueApi.theNext(id)
            _resultOfNextOrReturn.update { resultOfRequest }
        }
    }

    fun returnToQueue(queue: Queue) {
        viewModelScope.launch {
            _resultOfNextOrReturn.update { null }
            val currUser = userApi.getUser(auth.currentUser!!.uid)
            if (currUser is ResultOfRequest.Success) {
                val queueDTO = findQueueDTO(currUser.result!!.queues, queue.id)
                val resultOfRequest =
                    queueApi.addUserToQueue(
                        UserDTO(
                            currUser.result.id,
                            currUser.result.username,
                            queueDTO.admin
                        ), queue.id
                    )

                _resultOfNextOrReturn.update { resultOfRequest }
            }
        }
    }

    fun isAdmin(queue: Queue, userId: String = auth.currentUser!!.uid) {
        viewModelScope.launch {
            if (queue.allUsersAreAdmins) {
                _isCurrentUserAdmin.value = true
                return@launch
            }

            var flag = false
            val currUser = userApi.getUser(userId)
            if (currUser is ResultOfRequest.Success) {
                for (currQueue in currUser.result!!.queues) {
                    if (queue.id == currQueue.id) {
                        flag = currQueue.admin
                        break
                    }
                }
            }
            _isCurrentUserAdmin.value = flag
        }
    }

    fun kickOutUser(currQueue: Queue, currUser: UserDTO) {
        viewModelScope.launch {
            _resultOfKickOut.update { null }

            val resultOfRequest = queueApi.deleteUserFromQueue(currUser, currQueue.id)
            _resultOfKickOut.update { resultOfRequest }
        }
    }

    fun makeUserAdmin(index: Int, queue: Queue, userId: String) {
        viewModelScope.launch {
            _resultOfMakingAdmin.update { null }

            val result = userApi.getUser(userId)
            if (result is ResultOfRequest.Success) {
                for (currQueue in result.result!!.queues) {
                    if (currQueue.id == queue.id) {
                        currQueue.admin = true
                        break
                    }
                }
                userApi.updateQueuesOfCurrUser(result.result.queues, userId)
            }

            queue.users[index].admin = true
            val resultOfRequest = queueApi.makeUserAdmin(queue)

            _resultOfMakingAdmin.update { resultOfRequest }
        }
    }

    private fun findQueueDTO(queues: MutableList<QueueDTO>, id: String): QueueDTO {
        for (currQueue in queues) {
            if (id == currQueue.id) {
                return currQueue
            }
        }
        return QueueDTO()
    }

}