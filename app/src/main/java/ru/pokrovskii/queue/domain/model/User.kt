package ru.pokrovskii.queue.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val username: String,
    val password: String,
    var sessionToken: String
)
