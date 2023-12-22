package ru.pokrovskii.queue.core

sealed class ResultOfRequest {
    class Success : ResultOfRequest()
    class Error(val errorMessage: String): ResultOfRequest()
}