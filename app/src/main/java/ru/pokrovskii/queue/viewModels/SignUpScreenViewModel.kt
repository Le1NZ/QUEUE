package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpScreenViewModel : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userLogin = MutableStateFlow("")
    val userLogin: StateFlow<String> = _userLogin.asStateFlow()

    private val _userPassword = MutableStateFlow("")
    val userPassword: StateFlow<String> = _userPassword.asStateFlow()

    private val _userPasswordAgain = MutableStateFlow("")
    val userPasswordAgain: StateFlow<String> = _userPasswordAgain.asStateFlow()


    fun updateName(name: String) {
        _userName.value = name
    }

    fun updateLogin(login: String) {
        _userLogin.value = login
    }

    fun updatePassword(password: String) {
        _userPassword.value = password
    }

    fun updatePasswordAgain(passwordAgain: String) {
        _userPasswordAgain.value = passwordAgain
    }

}