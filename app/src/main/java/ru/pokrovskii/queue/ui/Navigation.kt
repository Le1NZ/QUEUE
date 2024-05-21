package ru.pokrovskii.queue.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.pokrovskii.queue.ui.screens.Screen
import ru.pokrovskii.queue.ui.screens.SplashScreen
import ru.pokrovskii.queue.ui.screens.logIn.SignInScreen
import ru.pokrovskii.queue.ui.screens.logIn.SignUpScreen
import ru.pokrovskii.queue.ui.screens.main.AddQueueScreen
import ru.pokrovskii.queue.ui.screens.main.HomeScreen
import ru.pokrovskii.queue.ui.screens.main.ProfileScreen
import ru.pokrovskii.queue.ui.screens.main.QueueScreen
import ru.pokrovskii.queue.ui.screens.main.SettingsScreen
import ru.pokrovskii.queue.viewModels.AddingQueueScreenViewModel
import ru.pokrovskii.queue.viewModels.MainScreenViewModel
import ru.pokrovskii.queue.viewModels.ProfileScreenViewModel
import ru.pokrovskii.queue.viewModels.QueueScreenViewModel
import ru.pokrovskii.queue.viewModels.SettingsScreenViewModel
import ru.pokrovskii.queue.viewModels.SignInScreenViewModel
import ru.pokrovskii.queue.viewModels.SignUpScreenViewModel
import ru.pokrovskii.queue.viewModels.SplashScreenViewModel

object Navigation {

    const val AUTH_ROUTE = "authRoute"
    const val MAIN_ROUTE = "mainRoute"
    const val SPLASH_ROUTE = "splashRoute"
    private const val TIME_TO_ANIMATED_NAVIGATION = 500

    @Composable
    fun Navigation(
        mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    ) {

        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = SPLASH_ROUTE,
            enterTransition = { fadeIn(animationSpec = tween(TIME_TO_ANIMATED_NAVIGATION)) },
            exitTransition = { fadeOut(animationSpec = tween(TIME_TO_ANIMATED_NAVIGATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(TIME_TO_ANIMATED_NAVIGATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(TIME_TO_ANIMATED_NAVIGATION)) },
        ) {
            navigation(
                startDestination = Screen.SplashScreen.name,
                route = SPLASH_ROUTE
            ) {
                composable(Screen.SplashScreen.name) {
                    val viewModel = hiltViewModel<SplashScreenViewModel>()
                    SplashScreen(
                        navController = navController,
                        viewModel = viewModel,
                        mainScreenViewModel = mainScreenViewModel
                    )
                }
            }
            navigation(
                startDestination = Screen.SignUpScreen.name,
                route = AUTH_ROUTE
            ) {
                composable(Screen.SignInScreen.name) {
                    val viewModel = hiltViewModel<SignInScreenViewModel>()
                    SignInScreen(navController, viewModel, mainScreenViewModel)
                }
                composable(Screen.SignUpScreen.name) {
                    val viewModel = hiltViewModel<SignUpScreenViewModel>()
                    SignUpScreen(navController, viewModel, mainScreenViewModel)
                }
            }
            navigation(
                startDestination = Screen.MainScreen.name,
                route = MAIN_ROUTE
            ) {
                composable(Screen.MainScreen.name) {
                    HomeScreen(navController, mainScreenViewModel)
                }
                composable(Screen.ProfileScreen.name) {
                    val viewModel = hiltViewModel<ProfileScreenViewModel>()
                    ProfileScreen(navController, viewModel)
                }
                composable(Screen.SettingsScreen.name) {
                    val viewModel = hiltViewModel<SettingsScreenViewModel>()
                    SettingsScreen(navController, viewModel)
                }
                composable(Screen.AddQueueScreen.name) {
                    val viewModel = hiltViewModel<AddingQueueScreenViewModel>()
                    AddQueueScreen(navController, viewModel)
                }
                composable(
                    route = Screen.QueueScreen.name + "/{id}",
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.StringType
                        }
                    )
                ) {
                    val id = it.arguments?.getString("id")
                    val viewModel = hiltViewModel<QueueScreenViewModel>()
                    QueueScreen(navController, viewModel, id ?: "")
                }
            }
        }
    }
}

