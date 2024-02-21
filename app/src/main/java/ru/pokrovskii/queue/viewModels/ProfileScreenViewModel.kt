package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val userDao : UserDAO,
    private val userApi : UserApi,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user : MutableStateFlow<User?> = MutableStateFlow(null)
    val user : StateFlow<User?> = _user

    private val _resultOfLogOut : MutableStateFlow<ResultOfRequest<Unit>?> = MutableStateFlow(null)
    val resultOfLogOut : StateFlow<ResultOfRequest<Unit>?> = _resultOfLogOut

    fun getUser() {
        viewModelScope.launch {
            _user.update { userDao.getCurrUser() }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            _resultOfLogOut.value = userApi.logOut()
            userDao.deleteUser(_user.value!!.username)
        }
    }

}