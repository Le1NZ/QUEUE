package ru.pokrovskii.queue.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.ui.screens.AlertDialog
import ru.pokrovskii.queue.ui.screens.Screen
import ru.pokrovskii.queue.ui.screens.ShowProgressBar
import ru.pokrovskii.queue.viewModels.AddingQueueScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQueueScreen(
    navController: NavController,
    viewModel: AddingQueueScreenViewModel = viewModel(),
) {

    var isShowingProgress by remember {
        mutableStateOf(false)
    }

    var isShowingAlertDialog by remember {
        mutableStateOf(false)
    }

    var isNameOfAddQueueIsEmpty by remember {
        mutableStateOf(true)
    }


    var isNameOfCreateQueueIsEmpty by remember {
        mutableStateOf(true)
    }

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var switchAdmins by remember {
        mutableStateOf(true)
    }

    val newQueueName: String by viewModel.nameOfNewQueue.collectAsState()
    val addQueueName: String by viewModel.nameOfAddQueue.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.adding_queue))
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
                })
        },

        ) { innerPadding ->
        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(innerPadding)
            ) {
                Tab(selected = selectedTabIndex == 0, onClick = {
                    selectedTabIndex = 0
                }) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(id = R.string.add)
                    )
                }
                Tab(selected = selectedTabIndex == 1, onClick = {
                    selectedTabIndex = 1
                }) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(id = R.string.create)
                    )
                }
            }

            // First tab ADDING
            if (selectedTabIndex == 0) {
                Column(
                    Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(id = R.string.code_of_queue),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        value = addQueueName,
                        singleLine = true,
                        maxLines = 1,
                        isError = isNameOfAddQueueIsEmpty,
                        onValueChange = {
                            viewModel.updateNameOfAddQueue(it)
                            isNameOfAddQueueIsEmpty = it.isEmpty()
                        },
                        label = {
                            Text(stringResource(id = R.string.enter_code))
                        },
                        supportingText = {
                            if (!isNameOfAddQueueIsEmpty) {
                                Text("")
                            } else {
                                Text(stringResource(id = R.string.field_should_be_not_empty))
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        onClick = {
                            if (!isNameOfAddQueueIsEmpty) {
                                isShowingProgress = true
                                viewModel.addQueue()
                            } else {
                                errorMessage = "The field is empty"
                                isShowingAlertDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Text(text = stringResource(id = R.string.add), fontSize = 16.sp)
                    }
                }
            } else { // Second tab CREATING
                Column(
                    Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = stringResource(id = R.string.name_of_queue),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        value = newQueueName,
                        isError = isNameOfCreateQueueIsEmpty,
                        maxLines = 1,
                        singleLine = true,
                        onValueChange = {
                            viewModel.updateNameOfNewQueue(it)
                            isNameOfCreateQueueIsEmpty = it.isEmpty()
                        },
                        label = {
                            Text(stringResource(id = R.string.enter_name))
                        },
                        supportingText = {
                            if (!isNameOfCreateQueueIsEmpty) {
                                Text("")
                            } else {
                                Text(stringResource(id = R.string.field_should_be_not_empty))
                            }
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.is_all_users_are_admin),
                            modifier = Modifier
                                .weight(1f),
                            fontSize = 16.sp
                        )

                        Switch(checked = switchAdmins, onCheckedChange = {
                            switchAdmins = it
                        })
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        onClick = {
                            if (!isNameOfCreateQueueIsEmpty) {
                                isShowingProgress = true
                                viewModel.createQueue(switchAdmins)
                            } else {
                                errorMessage = "The field is empty"
                                isShowingAlertDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Text(text = stringResource(id = R.string.add), fontSize = 16.sp)
                    }
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

            val resultOfCreateQueue = viewModel.resultOfCreateQueue.collectAsState().value
            LaunchedEffect(resultOfCreateQueue) {
                isShowingProgress = false
                when (resultOfCreateQueue) {
                    is ResultOfRequest.Success -> {
                        // Sharing to window of queue info about its id
                        navController.navigate(Screen.QueueScreen.name + "/${resultOfCreateQueue.result.id}")
                    }

                    is ResultOfRequest.Error -> {
                        errorMessage = resultOfCreateQueue.errorMessage
                        isShowingAlertDialog = true
                    }

                    else -> {}
                }
            }
        }

    }
}