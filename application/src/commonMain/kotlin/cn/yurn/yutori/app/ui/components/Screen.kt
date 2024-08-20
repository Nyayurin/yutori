package cn.yurn.yutori.app.ui.components

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.jetpack.navigatorViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.yurn.yutori.Adapter
import cn.yurn.yutori.app.Chat
import cn.yurn.yutori.app.ConnectScreenModel
import cn.yurn.yutori.app.MainViewModel
import cn.yurn.yutori.app.ScreenSize
import cn.yurn.yutori.app.onMessageCreated
import cn.yurn.yutori.module.adapter.satori.Satori
import cn.yurn.yutori.satori
import kotlinx.coroutines.launch

object ConnectScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel = navigatorViewModel<MainViewModel>()
        val (width, _) = viewModel.screen.size
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ConnectScreenModel() }
        var host by remember { mutableStateOf("") }
        var port by remember { mutableStateOf("") }
        var path by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        var requestChannels by remember { mutableStateOf(true) }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .safeContentPadding()
                .padding(
                    horizontal = animateDpAsState(
                        when (width) {
                            ScreenSize.Compact -> 32.dp
                            ScreenSize.Medium -> 64.dp
                            ScreenSize.Expanded -> 128.dp
                        },
                        TweenSpec(600)
                    ).value
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Yutori Application",
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = host,
                    onValueChange = { host = it },
                    singleLine = true,
                    label = { Text(text = "Host") },
                    modifier = Modifier.weight(0.75F)
                )
                TextField(
                    value = port,
                    onValueChange = { port = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Port") },
                    modifier = Modifier.weight(0.25F)
                )
            }
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
                    viewModel.viewModelScope.launch {
                        viewModel.satori?.stop()
                        viewModel.satori = satori {
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
                            client {
                                listening {
                                    message.created { onMessageCreated(viewModel) }
                                }
                            }
                        }
                        viewModel.satori!!.start()
                    }
                    navigator.push(HomeScreen)
                },
                modifier = Modifier.imePadding()
            ) {
                Text(
                    text = "Connect",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

object HomeScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigatorViewModel<MainViewModel>()
        val (width, _) = viewModel.screen.size
        val chats = viewModel.chats
        when (width) {
            ScreenSize.Compact -> {
                ChatMenu(
                    chats = chats,
                    onClick = { chat ->
                        chats[chats.indexOf(chat)] = chat.copy(unread = false)
                        navigator.push(ChattingScreen(chat) {
                            navigator.pop()
                        })
                    },
                    modifier = Modifier
                        .safeContentPadding()
                        .fillMaxSize()
                )
            }

            ScreenSize.Medium, ScreenSize.Expanded -> {
                Row(
                    modifier = Modifier
                        .safeContentPadding()
                        .fillMaxSize()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceBright,
                        modifier = Modifier.weight(0.3F)
                    ) {
                        ChatMenu(
                            chats = chats,
                            onClick = { chat ->
                                chats[chats.indexOf(chat)] = chat.copy(unread = false)
                                viewModel.chatting = chat
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(0.7F)) {
                        if (viewModel.chatting != null) {
                            ChattingScreen(viewModel.chatting!!) {
                                viewModel.chatting = null
                            }.Content()
                        }
                    }
                }
            }
        }
    }
}

class ChattingScreen(
    private val chat: Chat,
    private val onBack: () -> Unit = {}
) : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val viewModel = navigatorViewModel<MainViewModel>()

        Scaffold(
            topBar = {
                ChattingTopBar(
                    channelName = chat.name,
                    onBack = onBack
                )
            },
            bottomBar = {
                UserInput(
                    onMessageSent = { content ->
                        scope.launch {
                            viewModel.actions!!.message.create(
                                channel_id = chat.id,
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Messages(
                    messages = viewModel.messages[chat.id]!!,
                    scrollState = scrollState,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}