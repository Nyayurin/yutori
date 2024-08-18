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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectScreen : Screen {
    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    override fun Content() {
        val scope = remember { GlobalScope }
        val viewModel = viewModel<MainViewModel>()
        val (width, _) = viewModel.screen.size
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ConnectScreenModel() }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
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
        ) {
            Text(
                text = "Yutori App",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = screenModel.host,
                    onValueChange = { screenModel.host = it },
                    singleLine = true,
                    label = { Text(text = "Host") },
                    modifier = Modifier.weight(0.75F)
                )
                TextField(
                    value = screenModel.port.toString(),
                    onValueChange = { screenModel.port = it.toInt() },
                    singleLine = true,
                    label = { Text(text = "port") },
                    modifier = Modifier.weight(0.25F)
                )
            }
            TextField(
                value = screenModel.path,
                onValueChange = { screenModel.path = it },
                singleLine = true,
                label = { Text(text = "path") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = screenModel.token,
                onValueChange = { screenModel.token = it },
                singleLine = true,
                label = { Text(text = "token") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = screenModel.platform,
                    onValueChange = { screenModel.platform = it },
                    singleLine = true,
                    label = { Text(text = "platform") },
                    modifier = Modifier.weight(0.5F)
                )
                TextField(
                    value = screenModel.selfId,
                    onValueChange = { screenModel.selfId = it },
                    singleLine = true,
                    label = { Text(text = "self_id") },
                    modifier = Modifier.weight(0.5F)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Request channels",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = screenModel.requestChannels,
                    onCheckedChange = { screenModel.requestChannels = it }
                )
            }
            Button(
                onClick = {
                    scope.launch {
                        viewModel.satori?.stop()
                        viewModel.satori = satori {
                            install(Adapter.Satori) {
                                this.host = screenModel.host
                                this.port = screenModel.port
                                this.path = screenModel.path
                                this.token = screenModel.token
                                onConnect { _, service, satori ->
                                    cn.yurn.yutori.app.onConnect(
                                        viewModel,
                                        service,
                                        satori,
                                        screenModel.platform,
                                        screenModel.selfId,
                                        screenModel.requestChannels
                                    )
                                }
                            }
                            client {
                                listening {
                                    message.created { this.onMessageCreated(viewModel) }
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
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = viewModel<MainViewModel>()
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
                    }
                )
            }

            ScreenSize.Medium, ScreenSize.Expanded -> {
                Row(modifier = Modifier.fillMaxSize()) {
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
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        val viewModel = viewModel<MainViewModel>()

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