package ru.pokrovskii.queue.domain.DTO

data class QueueDTO(
    val id: String = "",
    val name: String = "",
    var admin: Boolean = false,
)