package ru.pokrovskii.queue.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.ui.Navigation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    var userLogin by remember {
        mutableStateOf("")
    }
    var userPassword by remember {
        mutableStateOf("")
    }
    var userPasswordAgain by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.LightGray, Color.Cyan)
                )
            )
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.welcome),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userLogin,
                onValueChange = { userLogin = it },
                label = {
                    Text(stringResource(id = R.string.enter_login))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userPassword,
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { userPassword = it },
                label = {
                    Text(stringResource(id = R.string.enter_password))
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            FilledTonalButton(
                onClick = {
                    navController.navigate(Screen.MainScreen.name) {
                        popUpTo(Navigation.AUTH_ROUTE)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(id = R.string.sign_in),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.already_havent_account))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.sign_up),
                Modifier.clickable {
                    navController.navigate(Screen.SignUpScreen.name)
                }
            )
        }
    }
}
