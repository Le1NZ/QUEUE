package ru.pokrovskii.queue.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.navigation.NavController
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.domain.DTO.QueueDTO
import ru.pokrovskii.queue.ui.screens.Screen
import ru.pokrovskii.queue.ui.screens.ShowProgressBar
import ru.pokrovskii.queue.viewModels.MainScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainScreenViewModel,
) {
    var queues by remember {
        mutableStateOf(listOf<QueueDTO>())
    }

    var isShowingProgress by remember {
        mutableStateOf(true)
    }

    var isShowingAlertDialog by remember {
        mutableStateOf(false)
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
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.ProfileScreen.name)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_account),
                            contentDescription = "Account button"
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddQueueScreen.name)
            }) {
                Icon(Icons.Filled.Add, "Floating action button")
            }
        }
    ) { innerPadding ->

        if (queues.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.no_queues), fontSize = 20.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(queues) { queue ->
                    QueueCard(queue, navController)
                }
            }
        }

        LaunchedEffect(viewModel.resultOfStarting) {
            viewModel.resultOfStarting.collect { result ->
                when (result) {
                    is ResultOfRequest.Success -> {
                        isShowingProgress = false
                        queues = result.result
                    }

                    is ResultOfRequest.Error -> {
                        isShowingProgress = false
                        errorMessage = result.errorMessage
                        isShowingAlertDialog = true
                    }

                    else -> {}
                }
            }
        }
        if (isShowingProgress) {
            ShowProgressBar {
                isShowingProgress = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QueueCard(queue: QueueDTO, navController: NavController) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = {
            navController.navigate(Screen.QueueScreen.name + "/${queue.id}")
        }
    ) {
        Text(
            text = queue.name, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}