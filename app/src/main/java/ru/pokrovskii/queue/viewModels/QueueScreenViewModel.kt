package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.QueueApi
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.QueueDAO
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.DTO.QueueDTO
import ru.pokrovskii.queue.domain.DTO.UserDTO
import ru.pokrovskii.queue.domain.model.Queue
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class QueueScreenViewModel @Inject constructor(
    private val queueApi: QueueApi,
    private val queueDAO: QueueDAO,
    private val userApi: UserApi,
    private val userDAO: UserDAO,
) : ViewModel() {

    private val _resultOfStarting: MutableStateFlow<ResultOfRequest<Queue>?> =
        MutableStateFlow(null)
    val resultOfStarting: StateFlow<ResultOfRequest<Queue>?> = _resultOfStarting

    private val _resultOfDeleting: MutableStateFlow<ResultOfRequest<Unit>?> = MutableStateFlow(null)
    val resultOfDeleting: StateFlow<ResultOfRequest<Unit>?> = _resultOfDeleting

    private val _resultOfNextOrReturn: MutableStateFlow<ResultOfRequest<Unit>?> =
        MutableStateFlow(null)
    val resultOfNextOfReturn: StateFlow<ResultOfRequest<Unit>?> = _resultOfNextOrReturn

    fun starting(id: String) {
        viewModelScope.launch {
            queueApi.startListeningQueue(id) { queue: Queue? ->
                _resultOfStarting.value = ResultOfRequest.Success(queue!!)
            }
        }
    }

    fun deleteQueue(currQueue: QueueDTO) {
        viewModelScope.launch {
            val currUser: User = userDAO.getCurrUser()!!
            val currUserDTO = UserDTO(currUser.username)
            var resultOfRequest1: ResultOfRequest<Unit> = ResultOfRequest.Loading
            var resultOfRequest2: ResultOfRequest<Unit> = ResultOfRequest.Loading
            launch {
                queueDAO.deleteQueue(currQueue.id)
            }
            val job1 = launch {
                resultOfRequest1 = userApi.deleteQueueFromUser(currQueue)
            }
            val job2 = launch {
                resultOfRequest2 = queueApi.deleteUserFromQueue(currUserDTO, currQueue.id)
            }
            launch {
                queueApi.endListening()
            }
            launch {
                var name = ""
                for (queue in currUser.queues) {
                    if (queue.id == currQueue.id) {
                        name = queue.name
                        break
                    }
                }
                currUser.queues.remove(QueueDTO(currQueue.id, name))
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
            var resultOfRequest: ResultOfRequest<Unit> = ResultOfRequest.Loading
            _resultOfNextOrReturn.update { null }

            val job = launch {
                resultOfRequest = queueApi.theNext(id)
            }

            launch {
                val currUser = userDAO.getCurrUser()
                if (currUser!!.queues.isNotEmpty())
                    currUser.queues.removeAt(0)
                userDAO.updateUser(currUser)
            }

            job.join()
            _resultOfNextOrReturn.update { resultOfRequest }
        }
    }

    fun returnToQueue(queue: Queue) {
        viewModelScope.launch {
            var resultOfRequest: ResultOfRequest<Unit> = ResultOfRequest.Loading
            _resultOfNextOrReturn.update { null }
            val currUser = userDAO.getCurrUser()
            val job = launch {
                resultOfRequest = queueApi.addUserToQueue(UserDTO(currUser!!.username), queue.id)
            }

            launch {
                currUser!!.queues.add(QueueDTO(queue.id, queue.name))
                userDAO.updateUser(currUser)
            }

            job.join()
            _resultOfNextOrReturn.update { resultOfRequest }
        }
    }
}