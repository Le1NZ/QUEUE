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
class SplashScreenViewModel @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDAO
) : ViewModel() {

    private val _result: MutableStateFlow<ResultOfRequest?> = MutableStateFlow(null)
    val result: StateFlow<ResultOfRequest?> = _result

    fun starting() {
        viewModelScope.launch {
            // Checking user in DB
            val currUser = userDao.getCurrUser()
            if (currUser == null) {
                _result.value = ResultOfRequest.Error("no_account")
            } else {
                // Checking info about user on server
                if (checkInfoOnServer(currUser)) {
                    _result.value = ResultOfRequest.Success()
                } else {
                    _result.value = ResultOfRequest.Error("error_from_server")
                }
            }
        }
    }

    suspend fun checkInfoOnServer(currUser: User): Boolean {
        val response = userApi.signIn(currUser.username, currUser.password)
        return response.isSuccessful
    }
}