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
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userDao: UserDAO,
    private val userApi: UserApi,
    private val auth: FirebaseAuth,
    private val queueApi: QueueApi,
) : ViewModel() {

    private val _newUserName: MutableStateFlow<String> = MutableStateFlow("")
    val newUserName: StateFlow<String> = _newUserName

    private val _currUser: MutableStateFlow<User?> = MutableStateFlow(null)

    private val _resultOfUpdateUserName: MutableStateFlow<ResultOfRequest<Unit?>?> =
        MutableStateFlow(null)
    val resultOfUpdateUserName: StateFlow<ResultOfRequest<Unit?>?> = _resultOfUpdateUserName

    fun updateUserName(name: String) {
        _newUserName.value = name
    }

    fun getUserName() {
        viewModelScope.launch {
            _currUser.value = userDao.getCurrUser()
            _newUserName.value = _currUser.value!!.username
        }
    }

    fun updateUserName() {
        viewModelScope.launch(Dispatchers.IO) {
            // Creating new instance of User class with new username and the same queues
            val oldUser = userDao.getCurrUser()!!
            val newUser = User(
                username = _newUserName.value,
                queues = oldUser.queues
            )


            val resultOfRequest = userApi.setUser(newUser, auth.currentUser!!.uid)
            _resultOfUpdateUserName.update { null }
            if (resultOfRequest is ResultOfRequest.Success) {
                // updating user in DB
                launch {
                    userDao.updateUser(newUser)
                }
                // updating name in queues
                launch {
                    queueApi.updateNameOfUserInQueues(oldUser, _newUserName.value)
                }
            }
            _resultOfUpdateUserName.update { resultOfRequest }
        }
    }
}
