package ru.pokrovskii.queue.data.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.domain.DTO.QueueDTO
import ru.pokrovskii.queue.domain.DTO.UserDTO
import ru.pokrovskii.queue.domain.model.Queue
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Inject

class UserApi @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
    }


    suspend fun signUp(email: String, password: String): ResultOfRequest<FirebaseUser> {
        var resultOfRequest: ResultOfRequest<FirebaseUser> = ResultOfRequest.Loading
        resultOfRequest = try {
            auth.createUserWithEmailAndPassword(email, password).await()
            ResultOfRequest.Success(auth.currentUser!!)
        } catch (e: Exception) {
            ResultOfRequest.Error(e.message!!)
        }

        return resultOfRequest
    }

    suspend fun signIn(email: String, password: String): ResultOfRequest<FirebaseUser> {
        var resultOfRequest: ResultOfRequest<FirebaseUser> = ResultOfRequest.Loading
        resultOfRequest = try {
            auth.signInWithEmailAndPassword(email, password).await()
            ResultOfRequest.Success(auth.currentUser!!)
        } catch (e: Exception) {
            ResultOfRequest.Error(e.message!!)
        }

        return resultOfRequest
    }

    suspend fun logOut(): ResultOfRequest<Unit> {
        auth.signOut()
        return ResultOfRequest.Success(Unit)
    }

    suspend fun setUser(user: User, userId: String): ResultOfRequest<Unit?> {
        var resultOfRequest: ResultOfRequest<Unit?> = ResultOfRequest.Loading
        try {
            database
                .collection(USERS_COLLECTION)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()
            resultOfRequest = ResultOfRequest.Success(Unit)
        } catch (e: Exception) {
            resultOfRequest = ResultOfRequest.Error(e.message!!)
        }

        return resultOfRequest
    }

    suspend fun getUser(userId: String): ResultOfRequest<User?> {
        var resultOfRequest: ResultOfRequest<User?> = ResultOfRequest.Loading
        try {
            val result = database
                .collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            if (result.exists()) {
                resultOfRequest = ResultOfRequest.Success(result.toObject<User>())
            } else {
                resultOfRequest = ResultOfRequest.Error("The object is not exist")
            }
        } catch (e: Exception) {
            resultOfRequest = ResultOfRequest.Error(e.message!!)
        }

        return resultOfRequest
    }

    suspend fun updateQueuesOfCurrUser(queues: MutableList<QueueDTO>): ResultOfRequest<Unit> {
        var resultOfRequest: ResultOfRequest<Unit> = ResultOfRequest.Loading
        try {
            database
                .collection(USERS_COLLECTION)
                .document(auth.currentUser!!.uid)
                .update("queues", queues)
                .await()

            resultOfRequest = ResultOfRequest.Success(Unit)
        } catch (e: Exception) {
            resultOfRequest = ResultOfRequest.Error(e.message!!)
        }

        return resultOfRequest
    }

    suspend fun deleteQueueFromUser(queue: QueueDTO): ResultOfRequest<Unit> {
        var resultOfRequest : ResultOfRequest<Unit> = ResultOfRequest.Loading

        try {
            database
                .collection(USERS_COLLECTION)
                .document(auth.currentUser!!.uid)
                .update("queues", FieldValue.arrayRemove(queue))
                .await()

            resultOfRequest = ResultOfRequest.Success(Unit)
        } catch (e: Exception) {
            resultOfRequest = ResultOfRequest.Error(e.message!!)
        }

        return resultOfRequest
    }

}