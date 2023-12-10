package ru.pokrovskii.queue.ui.screens

sealed class Screen(val name: String) {

    object MainScreen : Screen("main_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object AddQueueScreen : Screen("add_queue_screen")

}