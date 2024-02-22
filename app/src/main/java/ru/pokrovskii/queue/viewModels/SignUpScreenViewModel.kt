package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDAO,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _userPassword = MutableStateFlow("")
    val userPassword: StateFlow<String> = _userPassword

    private val _userPasswordAgain = MutableStateFlow("")
    val userPasswordAgain: StateFlow<String> = _userPasswordAgain

    private val _result: MutableStateFlow<ResultOfRequest<FirebaseUser?>?> = MutableStateFlow(null)
    val result: StateFlow<ResultOfRequest<FirebaseUser?>?> = _result

    fun updateName(name: String) {
        _userName.value = name
    }

    fun updateEmail(login: String) {
        _userEmail.value = login
    }

    fun updatePassword(password: String) {
        _userPassword.value = password
    }

    fun updatePasswordAgain(passwordAgain: String) {
        _userPasswordAgain.value = passwordAgain
    }

    fun signUpUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val resultOfRequest = userApi.signUp(_userEmail.value, _userPassword.value)
            _result.value = resultOfRequest
            if (resultOfRequest is ResultOfRequest.Success) {
                val currUser = User(username = _userName.value, id = resultOfRequest.result.uid)
                launch {
                    userApi.setUser(currUser, resultOfRequest.result.uid)
                }
                launch {
                    userDao.addUser(currUser)
                }
            }
        }
    }

}