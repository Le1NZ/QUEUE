package ru.pokrovskii.queue.ui.screens.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
import ru.pokrovskii.queue.domain.DTO.QueueDTO
import ru.pokrovskii.queue.domain.DTO.UserDTO
import ru.pokrovskii.queue.domain.model.Queue
import ru.pokrovskii.queue.ui.screens.Screen
import ru.pokrovskii.queue.ui.screens.ShowProgressBar
import ru.pokrovskii.queue.viewModels.QueueScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    navController: NavController,
    viewModel: QueueScreenViewModel = viewModel(),
    id: String,
) {

    var queue by remember {
        mutableStateOf(Queue())
    }

    val context = LocalContext.current
    val name = stringResource(id = R.string.queue)

    var title by remember {
        mutableStateOf(name)
    }

    var isShowingProgress by remember {
        mutableStateOf(false)
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
                    Text(text = title, fontWeight = FontWeight.Bold)
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
                        viewModel.deleteQueue(QueueDTO(queue.id, queue.name))
                        isShowingProgress = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete"
                        )
                    }

                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, queue.id)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = "Share"
                        )
                    }
                })
        },

        ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(queue.users) { index, user ->
                UserCard(user, index)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Button(
                    onClick = {
                        isShowingProgress = true
                        viewModel.returnToQueue(queue)
                    },
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    Text(text = stringResource(id = R.string.return_to_queue), fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = {
                        isShowingProgress = true
                        viewModel.theNextUser(queue.id)
                    },
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    Text(text = stringResource(id = R.string.the_next), fontSize = 16.sp)
                }
            }
            
        }


        if (isShowingProgress) {
            ShowProgressBar {
                isShowingProgress = false
            }
        }

        LaunchedEffect(Unit) {
            viewModel.starting(id)
            isShowingProgress = true
        }

        LaunchedEffect(viewModel.resultOfStarting) {
            viewModel.resultOfStarting.collect { result ->
                when (result) {
                    is ResultOfRequest.Success -> {
                        isShowingProgress = false
                        title = result.result.name
                        queue = result.result
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

        LaunchedEffect(viewModel.resultOfDeleting) {
            viewModel.resultOfDeleting.collect { result ->
                when (result) {
                    is ResultOfRequest.Success -> {
                        isShowingProgress = false
                        navController.navigate(Screen.MainScreen.name)
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

        LaunchedEffect(viewModel.resultOfNextOfReturn) {
            viewModel.resultOfNextOfReturn.collect { result ->
                when (result) {
                    is ResultOfRequest.Success -> {
                        isShowingProgress = false
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

    }
    BackHandler {
        navController.navigate(Screen.MainScreen.name)
    }
}

@Composable
private fun UserCard(user: UserDTO, index: Int) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = (index + 1).toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

    }
}