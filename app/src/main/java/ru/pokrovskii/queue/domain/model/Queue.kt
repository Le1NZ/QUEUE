package ru.pokrovskii.queue.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.pokrovskii.queue.domain.DTO.UserDTO

@Entity(tableName = "queues")
data class Queue(
    @PrimaryKey
    var id: String = "",
    val name: String = "Queue",
    @TypeConverters(ListOfUserToJsonConverter::class)
    val users: List<UserDTO> = listOf(),
)