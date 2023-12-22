package ru.pokrovskii.queue.ui

import android.os.Build
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.parse.Parse
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.ui.theme.QUEUETheme
import ru.pokrovskii.queue.viewModels.SplashScreenViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel : SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QUEUETheme {
                Navigation.Navigation()
            }
        }
    }
}
