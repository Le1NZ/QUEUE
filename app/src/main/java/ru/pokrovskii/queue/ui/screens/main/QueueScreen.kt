package ru.pokrovskii.queue.ui.screens.main

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.pokrovskii.queue.R
import ru.pokrovskii.queue.core.ResultOfRequest
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

    val isAdmin by viewModel.isCurrentUserAdmin.collectAsState()
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
                        navController.navigate(Screen.MainScreen.name) {
                            popUpTo(Screen.MainScreen.name)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back button"
                        )
                    }
                },
                actions = {
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

                    IconButton(onClick = {
                        viewModel.deleteQueue(queue)
                        isShowingProgress = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_exit),
                            contentDescription = "Exit"
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
                UserCard(user, index, viewModel, queue, isAdmin)
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
                    enabled = isAdmin,
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
                        viewModel.isAdmin(queue)
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
                        navController.navigate(Screen.MainScreen.name) {
                            popUpTo(Screen.MainScreen.name)
                        }
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
        navController.navigate(Screen.MainScreen.name) {
            popUpTo(Screen.MainScreen.name)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserCard(
    user: UserDTO,
    index: Int,
    viewModel: QueueScreenViewModel,
    queue: Queue,
    isAdmin: Boolean
) {
    var isShowingProgress by remember {
        mutableStateOf(false)
    }

    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    if (isAdmin || queue.allUsersAreAdmins) {
                        expanded = true
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                if (user.admin || queue.allUsersAreAdmins) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_crown),
                        contentDescription = "Admin"
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.make_admin)) },
                    onClick = {
                        isShowingProgress = true
                        viewModel.makeUserAdmin(index, queue, user.id)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_crown),
                            contentDescription = "Crown"
                        )
                    },
                    modifier = Modifier.padding(2.dp),
                    enabled = !user.admin && !queue.allUsersAreAdmins
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.kick_out)) },
                    onClick = {
                        isShowingProgress = true
                        viewModel.kickOutUser(queue, user)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete"
                        )
                    },
                    modifier = Modifier.padding(2.dp)
                )
            }
            Text(
                text = (index + 1).toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

    }

    if (isShowingProgress) {
        ShowProgressBar {
            isShowingProgress = false
        }
    }

    LaunchedEffect(viewModel.resultOfKickOut) {
        viewModel.resultOfKickOut.collect { result ->
            if (result is ResultOfRequest.Success || result is ResultOfRequest.Error) {
                isShowingProgress = false
                expanded = false
            }
        }
    }

    LaunchedEffect(viewModel.resultOfMakingAdmin) {
        viewModel.resultOfMakingAdmin.collect { result ->
            if (result is ResultOfRequest.Success || result is ResultOfRequest.Error) {
                isShowingProgress = false
                expanded = false
            }
        }
    }
}