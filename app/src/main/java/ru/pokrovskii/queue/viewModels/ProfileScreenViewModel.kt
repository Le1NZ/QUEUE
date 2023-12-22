package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val userDao : UserDAO
) : ViewModel() {

    private val _user : MutableStateFlow<User?> = MutableStateFlow(null)
    val user : StateFlow<User?> = _user

    fun getUser() {
        viewModelScope.launch {
            _user.value = userDao.getCurrUser()
        }
    }

}