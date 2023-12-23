package ru.pokrovskii.queue.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.pokrovskii.queue.core.Constants
import ru.pokrovskii.queue.data.dto.SignInDTO
import ru.pokrovskii.queue.data.dto.SignUpDTO
import ru.pokrovskii.queue.data.dto.UpdateUserDTO
import ru.pokrovskii.queue.data.dto.UserDTO
import ru.pokrovskii.queue.domain.model.User

interface UserApi {

    companion object {
        const val BASE_URL = "https://parseapi.back4app.com"
    }

    @Headers(
        "X-Parse-Application-Id: ${Constants.applicationId}",
        "X-Parse-REST-API-Key: ${Constants.restApiKey}",
        "X-Parse-Revocable-Session: 1",
        "Content-Type: application/json"
    )
    @POST("/users")
    suspend fun signUp(
        @Body user: User
    ): Response<SignUpDTO>

    @Headers(
        "X-Parse-Application-Id: ${Constants.applicationId}",
        "X-Parse-REST-API-Key: ${Constants.restApiKey}",
        "X-Parse-Revocable-Session: 1"
    )
    @GET("/login")
    suspend fun signIn(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<SignInDTO>

    @Headers(
        "X-Parse-Application-Id: ${Constants.applicationId}",
        "X-Parse-REST-API-Key: ${Constants.restApiKey}"
    )
    @GET("/users/{objectId}")
    suspend fun getUser(
        @Path("objectId") objectId: String
    ): Response<UserDTO>

    @Headers(
        "X-Parse-Application-Id: ${Constants.applicationId}",
        "X-Parse-REST-API-Key: ${Constants.restApiKey}"
    )
    @POST("/logout")
    suspend fun logOut(
        @Header("X-Parse-Session-Token") sessionToken: String
    ): Response<Unit>

    @Headers(
        "X-Parse-Application-Id: ${Constants.applicationId}",
        "X-Parse-REST-API-Key: ${Constants.restApiKey}",
        "Content-Type: application/json"
    )
    @PUT("/users/{objectId}")
    suspend fun updateUser(
        @Header("X-Parse-Session-Token") sessionToken: String,
        @Path("objectId") objectId: String,
        @Body user: User
    ): Response<UpdateUserDTO>

}