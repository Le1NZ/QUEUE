package ru.pokrovskii.queue.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.ui.screens.AlertDialog
import ru.pokrovskii.queue.ui.screens.Screen
import ru.pokrovskii.queue.ui.screens.ShowProgressBar
import ru.pokrovskii.queue.viewModels.SettingsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsScreenViewModel = viewModel()
) {
    val context = LocalContext.current

    var isShowingProgress by remember {
        mutableStateOf(true)
    }
    var isShowingAlertDialog by remember {
        mutableStateOf(false)
    }
    var errorMessage by remember {
        mutableStateOf("")
    }
    var isUserNameEmpty by remember {
        mutableStateOf(false)
    }
    val userName: String by viewModel.newUserName.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(16.dp),
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.ProfileScreen.name)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back button"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.updateUserName()
                        isShowingProgress = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_mark),
                            contentDescription = "Check mark"
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
                    text = stringResource(id = R.string.your_username),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = userName,
                    isError = isUserNameEmpty,
                    onValueChange = {
                        viewModel.updateUserName(it)
                        isUserNameEmpty = it.isEmpty()
                    },
                    label = {
                        Text(stringResource(id = R.string.enter_name))
                    },
                    supportingText = {
                        if (!isUserNameEmpty) {
                            Text("")
                        } else {
                            Text(stringResource(id = R.string.field_should_be_not_empty))
                        }
                    }
                )
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

        LaunchedEffect(Unit) {
            viewModel.getUserName()
            isShowingProgress = false
        }

        LaunchedEffect(viewModel.resultOfUpdateUserName) {
            viewModel.resultOfUpdateUserName.collect { result ->
                isShowingProgress = false
                when (result) {
                    is ResultOfRequest.Success -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.success_update_username),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ResultOfRequest.Error -> {
                        errorMessage = result.errorMessage
                        isShowingAlertDialog = true
                    }

                    else -> {}
                }
            }
        }

    }
}