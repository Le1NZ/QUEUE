package ru.pokrovskii.queue.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.ui.Navigation
import ru.pokrovskii.queue.viewModels.MainScreenViewModel
import ru.pokrovskii.queue.viewModels.SplashScreenViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashScreenViewModel = viewModel(),
    mainScreenViewModel: MainScreenViewModel,
) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.starting()
        }

        launch {
            val job = launch {
                scale.animateTo(
                    targetValue = 0.7f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }

            job.join()
            viewModel.result.collect { result ->
                navigationToNextScreen(result, navController, mainScreenViewModel)
            }
        }


    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }

}

fun navigationToNextScreen(result: ResultOfRequest<FirebaseUser?>?, navController: NavController, mainScreenViewModel: MainScreenViewModel) {
    when (result) {
        is ResultOfRequest.Error -> {
            navController.navigate(Navigation.AUTH_ROUTE) {
                popUpTo(Navigation.SPLASH_ROUTE)
            }
        }

        is ResultOfRequest.Success -> {
            navController.navigate(Navigation.MAIN_ROUTE) {
                popUpTo(Navigation.SPLASH_ROUTE)
                mainScreenViewModel.starting()
            }
        }

        else -> {}
    }
}