package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.QueueApi
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userDao: UserDAO,
    private val userApi: UserApi,
    private val queueApi: QueueApi,
) : ViewModel() {

    private val _newUserName: MutableStateFlow<String> = MutableStateFlow("")
    val newUserName: StateFlow<String> = _newUserName

    private val _resultOfUpdateUserName: MutableStateFlow<ResultOfRequest<Unit?>?> =
        MutableStateFlow(null)
    val resultOfUpdateUserName: StateFlow<ResultOfRequest<Unit?>?> = _resultOfUpdateUserName

    fun updateUserName(name: String) {
        _newUserName.value = name
    }

    fun getUserName() {
        viewModelScope.launch {
            val currUser = userDao.getCurrUser()
            _newUserName.value = currUser!!.username
        }
    }

    fun updateUserName() {
        viewModelScope.launch(Dispatchers.IO) {
            // Creating new instance of User class with new username and the same queues
            val oldUser = userDao.getCurrUser()!!
            oldUser.username = _newUserName.value

            _resultOfUpdateUserName.update { null }
            val resultOfRequest = userApi.setUser(oldUser, oldUser.id)
            if (resultOfRequest is ResultOfRequest.Success) {
                // updating user in DB
                launch {
                    userDao.updateUser(oldUser)
                }
                // updating name in queues
                launch {
                    queueApi.updateNameOfUserInQueues(oldUser, oldUser.username)
                }
            }
            _resultOfUpdateUserName.update { resultOfRequest }
        }
    }
}
