package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _userEmail = MutableStateFlow("")
    val userName = _userEmail.asStateFlow()

    private val _userPassword = MutableStateFlow("")
    val userPassword = _userPassword.asStateFlow()

    private val _result: MutableStateFlow<ResultOfRequest<FirebaseUser>?> = MutableStateFlow(null)
    val result: StateFlow<ResultOfRequest<FirebaseUser>?> = _result

    fun updateEmail(name: String) {
        _userEmail.value = name
    }

    fun updatePassword(password: String) {
        _userPassword.value = password
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val resultOfRequest = userApi.signIn(_userEmail.value, _userPassword.value)

            if (resultOfRequest is ResultOfRequest.Success) {
                val resultOfRequestOfUserData = userApi.getUser(resultOfRequest.result.uid)
                if (resultOfRequestOfUserData is ResultOfRequest.Success) {
                    userDao.addUser(resultOfRequestOfUserData.result!!)
                    _result.update { resultOfRequest }
                } else if (resultOfRequestOfUserData is ResultOfRequest.Error) {
                    _result.update { ResultOfRequest.Error(resultOfRequestOfUserData.errorMessage) }
                }
            } else {
                _result.update { resultOfRequest }
            }
        }
    }

}