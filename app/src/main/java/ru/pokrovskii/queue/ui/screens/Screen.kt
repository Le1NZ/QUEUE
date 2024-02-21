package ru.pokrovskii.queue.ui.screens

sealed class Screen(val name: String) {

    object MainScreen : Screen("main_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object AddQueueScreen : Screen("add_queue_screen")
    object SplashScreen : Screen("splash_screen")
    object ProfileScreen : Screen("profile_screen")
    object SettingsScreen : Screen("settings_screen")
    object QueueScreen : Screen("queue_screen")

}