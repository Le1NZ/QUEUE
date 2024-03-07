package ru.pokrovskii.queue.viewModels

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.core.NotificationService
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.domain.DTO.QueueDTO
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userApi: UserApi,
) : ViewModel() {

    private val _resultOfStarting: MutableStateFlow<ResultOfRequest<List<QueueDTO>>?> =
        MutableStateFlow(null)
    val resultOfStarting: StateFlow<ResultOfRequest<List<QueueDTO>>?> = _resultOfStarting

    fun starting(context: Context) {
        if (_resultOfStarting.value == null) {
            val intent = Intent(context, NotificationService::class.java)
            context.startService(intent)
            viewModelScope.launch {
                userApi.startListeningQueues(auth.currentUser!!.uid) { user: User? ->
                    _resultOfStarting.update { ResultOfRequest.Success(user!!.queues) }
                }
            }
        }
    }

}