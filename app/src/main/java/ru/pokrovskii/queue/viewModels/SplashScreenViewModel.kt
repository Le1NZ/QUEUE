package ru.pokrovskii.queue.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userApi: UserApi,
    private val userDao: UserDAO,
) : ViewModel() {

    private val _result: MutableStateFlow<ResultOfRequest<FirebaseUser?>?> = MutableStateFlow(null)
    val result: StateFlow<ResultOfRequest<FirebaseUser?>?> = _result

    fun starting() {
        viewModelScope.launch {
            if (auth.currentUser != null) {
                val resultOfRequest = userApi.getUser(auth.currentUser!!.uid)
                if (resultOfRequest is ResultOfRequest.Success) {
                    launch {
                        userDao.updateUser(resultOfRequest.result!!)
                    }
                    _result.value = ResultOfRequest.Success(auth.currentUser)
                } else if (resultOfRequest is ResultOfRequest.Error) {
                    _result.value = ResultOfRequest.Error(resultOfRequest.errorMessage)
                }
            } else {
                _result.value = ResultOfRequest.Error("no sign in")
            }
        }
    }

}