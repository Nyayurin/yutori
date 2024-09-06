package cn.yurn.yutori.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import cn.yurn.yutori.Adapter
import cn.yurn.yutori.app.ConnectScreenModel
import cn.yurn.yutori.app.MainViewModel
import cn.yurn.yutori.app.ScreenSize
import cn.yurn.yutori.app.onMessageCreated
import cn.yurn.yutori.app.platformSatoriAsync
import cn.yurn.yutori.module.adapter.satori.Satori
import cn.yurn.yutori.yutori
import kotlinx.coroutines.launch

@Composable
fun ConnectScreen(navController: NavController, viewModel: MainViewModel) {
    val (width, _) = viewModel.screen.size
    val screenModel = remember { ConnectScreenModel() }
    var host by remember { mutableStateOf(screenModel.host) }
    var port by remember { mutableStateOf(screenModel.port.toString()) }
    var path by remember { mutableStateOf(screenModel.path) }
    var token by remember { mutableStateOf(screenModel.token) }
    var requestChannels by remember { mutableStateOf(screenModel.requestChannels) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .safeDrawingPadding()
            .padding(
                horizontal = animateDpAsState(
                    when (width) {
                        ScreenSize.Compact -> 16.dp
                        ScreenSize.Medium -> 32.dp
                        ScreenSize.Expanded -> 64.dp
                    },
                    tween(600)
                ).value
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AnimatedContent(width) {
            Text(
                text = "Yutori Application",
                style = when (width) {
                    ScreenSize.Compact -> MaterialTheme.typography.titleLarge
                    ScreenSize.Medium -> MaterialTheme.typography.displaySmall
                    ScreenSize.Expanded -> MaterialTheme.typography.displayLarge
                }
            )
        }
        TextField(
            value = host,
            onValueChange = { host = it },
            singleLine = true,
            label = { Text(text = "Host") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = port,
            onValueChange = { port = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = { Text(text = "Port") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = path,
            onValueChange = { path = it },
            singleLine = true,
            label = { Text(text = "Path") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = token,
            onValueChange = { token = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Token") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Request channels")
            Switch(
                checked = requestChannels,
                onCheckedChange = { requestChannels = it }
            )
        }
        Button(
            onClick = {
                screenModel.host = host
                screenModel.port = port.toInt()
                screenModel.path = path
                screenModel.token = token
                screenModel.requestChannels = requestChannels
                viewModel.yutori?.stop()
                viewModel.yutori = yutori {
                    install(Adapter.Satori) {
                        this.host = screenModel.host
                        this.port = screenModel.port
                        this.path = screenModel.path
                        this.token = screenModel.token
                        onConnect { logins, service, satori ->
                            cn.yurn.yutori.app.onConnect(
                                viewModel,
                                logins,
                                service,
                                satori,
                                screenModel.requestChannels
                            )
                        }
                    }
                    adapter {
                        listening {
                            message.created { onMessageCreated(viewModel) }
                        }
                    }
                }
                platformSatoriAsync(viewModel.viewModelScope, viewModel.yutori!!)
                navController.navigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            Text(
                text = "Connect",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val (width, _) = viewModel.screen.size
    val chats = viewModel.chats
    Row(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
    ) {
        Surface(
            color = animateColorAsState(
                when (width) {
                    ScreenSize.Compact -> Color.Unspecified
                    else -> MaterialTheme.colorScheme.surfaceBright
                },
                tween(600)
            ).value,
            modifier = Modifier
                .fillMaxWidth(
                    animateFloatAsState(
                        when (width) {
                            ScreenSize.Compact -> 1F
                            else -> 0.3F
                        },
                        tween(600)
                    ).value
                )
                .fillMaxHeight()
        ) {
            ChatMenu(
                chats = chats,
                viewModel = viewModel,
                onClick = { chat ->
                    chats[chats.indexOf(chat)] = chat.copy(unread = false)
                    viewModel.chatting = chat
                    if (width == ScreenSize.Compact) {
                        navController.navigate("chatting/${chat.id}")
                    }
                    viewModel.update()
                }
            )
        }
        if (width != ScreenSize.Compact) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (viewModel.chatting != null) {
                    ChattingScreen(navController, viewModel, viewModel.chatting!!.id)
                }
            }
        }
    }
}

@Composable
fun ChattingScreen(navController: NavController, viewModel: MainViewModel, channelId: String) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val (width, _) = viewModel.screen.size
    val chatting = viewModel.chats.find { it.id == channelId }!!

    Scaffold(
        topBar = {
            ChattingTopBar(
                channelName = chatting.name,
                onBack = {
                    when (width) {
                        ScreenSize.Compact -> navController.popBackStack()
                        else -> viewModel.chatting = null
                    }
                }
            )
        },
        bottomBar = {
            UserInput(
                onMessageSent = { content ->
                    scope.launch {
                        viewModel.actions!!.message.create(
                            channel_id = chatting.id,
                            content = content
                        )
                    }
                },
                resetScroll = {
                    scope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                }
            )
        }
    ) { paddingValues ->
        Messages(
            messages = viewModel.messages[chatting.id]!!,
            chatting = chatting,
            scrollState = scrollState,
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}