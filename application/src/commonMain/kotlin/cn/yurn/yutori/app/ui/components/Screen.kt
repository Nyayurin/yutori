package cn.yurn.yutori.app.ui.components

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import cn.yurn.yutori.Adapter
import cn.yurn.yutori.app.Chat
import cn.yurn.yutori.app.MainViewModel
import cn.yurn.yutori.app.ScreenSize
import cn.yurn.yutori.app.onMessageCreated
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.module.adapter.satori.Satori
import cn.yurn.yutori.satori
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectScreen : Screen {
    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    override fun Content() {
        val scope = remember { GlobalScope }
        val viewModel = viewModel<MainViewModel>()
        val (width, height) = viewModel.screen.size
        val navigator = LocalNavigator.currentOrThrow
        var host by remember { mutableStateOf("") }
        var port by remember { mutableIntStateOf(5500) }
        var path by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        var platform by remember { mutableStateOf("") }
        var selfId by remember { mutableStateOf("") }
        var requestChannels by remember { mutableStateOf(true) }

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
                    value = host,
                    onValueChange = { host = it },
                    singleLine = true,
                    label = { Text(text = "Host") },
                    modifier = Modifier.weight(0.75F)
                )
                TextField(
                    value = port.toString(),
                    onValueChange = { port = it.toInt() },
                    singleLine = true,
                    label = { Text(text = "port") },
                    modifier = Modifier.weight(0.25F)
                )
            }
            TextField(
                value = path,
                onValueChange = { path = it },
                singleLine = true,
                label = { Text(text = "path") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = token,
                onValueChange = { token = it },
                singleLine = true,
                label = { Text(text = "token") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = platform,
                    onValueChange = { platform = it },
                    singleLine = true,
                    label = { Text(text = "platform") },
                    modifier = Modifier.weight(0.5F)
                )
                TextField(
                    value = selfId,
                    onValueChange = { selfId = it },
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
                    checked = requestChannels,
                    onCheckedChange = { requestChannels = it }
                )
            }
            Button(
                onClick = {
                    scope.launch {
                        viewModel.satori?.stop()
                        viewModel.satori = satori {
                            install(Adapter.Satori) {
                                this.host = host
                                this.port = port
                                this.path = path
                                this.token = token
                                onConnect { _, service, satori ->
                                    cn.yurn.yutori.app.onConnect(
                                        viewModel,
                                        service,
                                        satori,
                                        platform,
                                        selfId,
                                        requestChannels
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
        val (width, height) = viewModel.screen.size
        val chats = viewModel.chats
        when (width) {
            ScreenSize.Compact -> {
                ChatMenu(
                    chats = chats,
                    onClick = { chat ->
                        chats[chats.indexOf(chat)] = chat.copy(unread = false)
                        navigator.push(ChattingScreen(chat) {
                            viewModel.chats[viewModel.chats.indexOf(chat)] = chat.copy(unread = false)
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
                                val chat = viewModel.chats.find { it.id == viewModel.chatting!!.id }
                                if (chat != null) {
                                    viewModel.chats[viewModel.chats.indexOf(chat)] =
                                        chat.copy(unread = false)
                                }
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
    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()
        var image by remember { mutableStateOf<Image?>(null) }
        var scale by remember { mutableFloatStateOf(1F) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val state = rememberTransformableState { zoomChange, panChange, _ ->
            scale *= zoomChange
            offset += panChange
        }
        val navigator = LocalNavigator.currentOrThrow
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
                    onImageClick = { element ->
                        image = element
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val onDisableImageView = {
            image = null
            scale = 1F
            offset = Offset.Zero
        }

        if (image != null) {
            Surface(
                color = Color(0F, 0F, 0F, 0.75F),
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = state, lockRotationOnZoomPan = true)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onDisableImageView
                    )
            ) {
                AsyncImage(
                    model = image!!.src,
                    contentDescription = null,
                    imageLoader = ImageLoader(LocalPlatformContext.current),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            }
        }

        BackHandler(true) {
            if (image != null) {
                onDisableImageView()
            } else {
                val chat = viewModel.chats.find { it.id == chat.id }
                if (chat != null) {
                    viewModel.chats[viewModel.chats.indexOf(chat)] = chat.copy(unread = false)
                }
                navigator.pop()
            }
        }
    }
}