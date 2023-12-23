package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userDao: UserDAO,
    private val userApi: UserApi
) : ViewModel() {

    private val _newUserName: MutableStateFlow<String> = MutableStateFlow("")
    val newUserName: StateFlow<String> = _newUserName

    private val _currUser: MutableStateFlow<User?> = MutableStateFlow(null)

    private val _resultOfUpdateUserName: MutableStateFlow<ResultOfRequest?> = MutableStateFlow(null)
    val resultOfUpdateUserName : StateFlow<ResultOfRequest?> = _resultOfUpdateUserName

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
        viewModelScope.launch {
            val newUser = User(
                _newUserName.value,
                _currUser.value!!.password,
                _currUser.value!!.sessionToken,
                _currUser.value!!.objectId
            )
            val response = userApi.updateUser(
                _currUser.value!!.sessionToken,
                _currUser.value!!.objectId,
                newUser
            )
            if (response.isSuccessful) {
                val responseSignIn = userApi.signIn(newUser.username, newUser.password)
                if (responseSignIn.isSuccessful) {
                    newUser.sessionToken = responseSignIn.body()!!.sessionToken
                    userDao.updateUser(newUser)
                    _currUser.value = newUser
                    _resultOfUpdateUserName.value = ResultOfRequest.Success()
                } else {
                    _resultOfUpdateUserName.value = ResultOfRequest.Error(
                        responseSignIn.errorBody()!!.string()
                    )
                }
            } else {
                _resultOfUpdateUserName.value = ResultOfRequest.Error(
                    response.errorBody()!!.string()
                )
            }
        }
    }

}