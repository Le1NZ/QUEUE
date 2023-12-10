package ru.pokrovskii.queue.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.pokrovskii.queue.ui.theme.QUEUETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QUEUETheme {
                Navigation.Navigation()
            }
        }
    }
}
