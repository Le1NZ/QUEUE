package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class SignInScreenViewModel @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDAO
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _userPassword = MutableStateFlow("")
    val userPassword = _userPassword.asStateFlow()

    private val _result: MutableStateFlow<ResultOfRequest?> = MutableStateFlow(null)
    val result: StateFlow<ResultOfRequest?> = _result

    fun updateEmail(name: String) {
        _userName.value = name
    }

    fun updatePassword(password: String) {
        _userPassword.value = password
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userApi.signIn(_userName.value, _userPassword.value)
            if (response.isSuccessful) {
                val userResponse = userApi.getUser(response.body()!!.objectId)
                if (userResponse.isSuccessful) {
                    val currUser = User(
                        _userName.value,
                        _userPassword.value,
                        response.body()!!.sessionToken,
                        response.body()!!.objectId
                    )
                    userDao.addUser(currUser)
                    _result.value = ResultOfRequest.Success()
                } else {
                    _result.value = ResultOfRequest.Error(response.errorBody()!!.string())
                }
            } else {
                _result.value = ResultOfRequest.Error(response.errorBody()!!.string())
            }
        }
    }

}