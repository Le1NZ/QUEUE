package ru.pokrovskii.queue.data.dto

data class SignInDTO(
    val objectId: String,
    val username: String,
    val createdAt: String,
    val updatedAt: String,
    val sessionToken: String
)
