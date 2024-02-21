package ru.pokrovskii.queue.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.domain.model.User
import ru.pokrovskii.queue.ui.Navigation
import ru.pokrovskii.queue.ui.screens.AlertDialog
import ru.pokrovskii.queue.ui.screens.Screen
import ru.pokrovskii.queue.ui.screens.ShowProgressBar
import ru.pokrovskii.queue.viewModels.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileScreenViewModel = viewModel()
) {

    var isShowingProgress by remember {
        mutableStateOf(true)
    }
    var isShowingAlertDialog by remember {
        mutableStateOf(false)
    }
    var currUser: User? by remember {
        mutableStateOf(null)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.profile))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.MainScreen.name)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back button"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.SettingsScreen.name)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                })
        },

        ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = if (currUser != null) currUser!!.username else "",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = {
                        viewModel.logOut()
                        isShowingProgress = true
                    },
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    Text(text = stringResource(id = R.string.log_out), fontSize = 16.sp)
                }
            }

            if (isShowingProgress) {
                ShowProgressBar {
                    isShowingProgress = false
                }
            }

            if (isShowingAlertDialog) {
                AlertDialog(
                    onClick = {
                        isShowingAlertDialog = false
                    },
                    dialogTitle = stringResource(id = R.string.error),
                    dialogText = errorMessage
                )
            }

            LaunchedEffect(viewModel.user) {
                if (currUser == null) {
                    launch(Dispatchers.IO) {
                        viewModel.getUser()
                    }
                }
                viewModel.user.collect { user ->
                    isShowingProgress = false
                    currUser = user
                }
            }

            LaunchedEffect(viewModel.resultOfLogOut) {
                viewModel.resultOfLogOut.collect { result ->
                    isShowingProgress = false
                    when (result) {
                        is ResultOfRequest.Error -> {
                            errorMessage = result.errorMessage
                            isShowingAlertDialog = true
                        }

                        is ResultOfRequest.Success -> {
                            navController.navigate(Navigation.AUTH_ROUTE) {
                                popUpTo(Navigation.MAIN_ROUTE)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

    }
}