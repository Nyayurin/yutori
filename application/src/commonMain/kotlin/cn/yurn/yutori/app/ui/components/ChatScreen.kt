package cn.yurn.yutori.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.navigatorViewModel
import cn.yurn.yutori.Message
import cn.yurn.yutori.app.MainViewModel
import cn.yurn.yutori.message.element.At
import cn.yurn.yutori.message.element.Audio
import cn.yurn.yutori.message.element.Author
import cn.yurn.yutori.message.element.Bold
import cn.yurn.yutori.message.element.Br
import cn.yurn.yutori.message.element.Button
import cn.yurn.yutori.message.element.Code
import cn.yurn.yutori.message.element.Delete
import cn.yurn.yutori.message.element.Em
import cn.yurn.yutori.message.element.File
import cn.yurn.yutori.message.element.Href
import cn.yurn.yutori.message.element.Idiomatic
import cn.yurn.yutori.message.element.Image
import cn.yurn.yutori.message.element.Ins
import cn.yurn.yutori.message.element.MessageElement
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import cn.yurn.yutori.satori
import cn.yurn.yutori.toElements
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingTopBar(
    channelName: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onMenu: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp))
            .shadow(8.dp),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = channelName,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = onMenu) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun Messages(
    messages: List<Message>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        reverseLayout = true,
        state = scrollState,
        modifier = modifier.fillMaxSize()
    ) {
        val now = Clock.System.now().toEpochMilliseconds()
        items(
            items = messages.sortedWith { o1, o2 ->
                (
                        (o1.created_at?.toLong() ?: now) -
                                (o2.created_at?.toLong() ?: now)
                        ).toInt()
            }.reversed(),
            key = { message -> message.id }
        ) { message ->
            Message(message = message)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit = {},
    resetScroll: () -> Unit = {},
) {
    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 2.dp,
        contentColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .imePadding()
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            UserInputText(
                onTextChanged = { textState = it },
                textFieldValue = textState,
                // Close extended selector if text field receives focus
                onTextFieldFocused = { textFieldFocusState = it },
                focusState = textFieldFocusState
            )
            UserInputSelector(
                sendMessageEnabled = textState.text.isNotBlank(),
                onMessageSent = {
                    onMessageSent(textState.text)
                    // Reset text field and close keyboard
                    textState = TextFieldValue()
                    // Move scroll to bottom
                    resetScroll()
                }
            )
        }
    }
}

@Composable
private fun UserInputSelector(
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onMessageSent,
            modifier = Modifier.height(36.dp),
            enabled = sendMessageEnabled,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "发送",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(Modifier.fillMaxSize()) {
            UserInputTextField(
                textFieldValue,
                onTextChanged,
                onTextFieldFocused,
                focusState
            )
        }
    }
}

@Composable
private fun BoxScope.UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    modifier: Modifier = Modifier
) {
    var lastFocusState by remember { mutableStateOf(false) }
    BasicTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .fillMaxSize()
            .align(Alignment.CenterStart)
            .onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
    )

    val disableContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    if (textFieldValue.text.isEmpty() && !focusState) {
        Text(
            text = "Message",
            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
        )
    }
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun Message(message: Message) {
    val viewModel = navigatorViewModel<MainViewModel>()
    Row(modifier = Modifier.padding(top = 8.dp)) {
        val imageLoader = ImageLoader(LocalPlatformContext.current)
        if (message.user!!.id == viewModel.self!!.id.substring("private:".length)) {
            AuthorAndTextMessage(
                message = message,
                isUserMe = true,
                modifier = Modifier
                    .padding(start = 64.dp)
                    .weight(1f)
            )
            AsyncImage(
                model = message.user!!.avatar,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(50.dp)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top)
            )
        } else {
            AsyncImage(
                model = message.user!!.avatar,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(50.dp)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top)
            )
            AuthorAndTextMessage(
                message = message,
                isUserMe = false,
                modifier = Modifier
                    .padding(end = 64.dp)
                    .weight(1f)
            )
        }
    }
}


@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun AuthorAndTextMessage(
    message: Message,
    isUserMe: Boolean,
    modifier: Modifier = Modifier
) {
    val viewModel = navigatorViewModel<MainViewModel>()
    Column(
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = message.member?.nick ?: message.user?.nick ?: message.user?.name
            ?: if (isUserMe) viewModel.self!!.name else
                if (viewModel.chatting!!.id.startsWith("private:")) viewModel.chatting!!.name else "null",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .paddingFrom(LastBaseline, after = 8.dp)
        )
        ClickableMessage(message, isUserMe)
        Spacer(Modifier.height(8.dp))
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val SelfChatBubbleShape = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun ClickableMessage(message: Message, isUserMe: Boolean) {
    val viewModel = navigatorViewModel<MainViewModel>()
    val messages = mutableListOf<MutableList<MessageElement>>(mutableListOf())
    val elements = message.content
        .replace("\r\n", "<br>")
        .replace("\r", "<br>")
        .replace("\n", "<br>")
        .toElements(viewModel.satori ?: satori { })
    for ((index, element) in elements.withIndex()) when (element) {
        is cn.yurn.yutori.message.element.Text -> messages.last() += element
        is At -> messages.last() += element
        is Sharp -> messages.last() += element
        is Href -> messages.last() += element
        is Image -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Audio -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Video -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is File -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Bold, is Strong -> messages.last() += element
        is Idiomatic, is Em -> messages.last() += element
        is Underline, is Ins -> messages.last() += element
        is Strikethrough, is Delete -> messages.last() += element
        is Spl -> messages.last() += element
        is Code -> messages.last() += element
        is Sup -> messages.last() += element
        is Sub -> messages.last() += element
        is Br -> messages += mutableListOf<MessageElement>()
        is Paragraph -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages += mutableListOf<MessageElement>()
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
                messages += mutableListOf<MessageElement>()
            }
        }

        is cn.yurn.yutori.message.element.Message -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Quote -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Author -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        is Button -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }

        else -> {
            if (messages.last().isNotEmpty()) {
                messages += mutableListOf<MessageElement>()
            }
            messages.last() += element
            if (elements.size != index + 1) {
                messages += mutableListOf<MessageElement>()
            }
        }
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(8.dp),
        shape = if (isUserMe) SelfChatBubbleShape else ChatBubbleShape
    ) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(modifier = Modifier.padding(16.dp)) {
                for (column in messages) {
                    if (column.size <= 1) {
                        if (column.isEmpty()) {
                            BrElement()
                        } else {
                            SelectElement(column[0])
                        }
                    } else {
                        Row {
                            for (element in column) SelectElement(element)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectElement(element: MessageElement) {
    when (element) {
        is cn.yurn.yutori.message.element.Text -> TextElement(element)
        is At -> AtElement(element)
        is Sharp -> SharpElement(element)
        is Href -> HrefElement(element)
        is Image -> ImageElement(element)
        is Audio -> AudioElement(element)
        is Video -> VideoElement(element)
        is File -> FileElement(element)
        is Bold -> BoldElement(element)
        is Strong -> StrongElement(element)
        is Idiomatic -> IdiomaticElement(element)
        is Em -> EmElement(element)
        is Underline -> UnderlineElement(element)
        is Ins -> InsElement(element)
        is Strikethrough -> StrikethroughElement(element)
        is Delete -> DeleteElement(element)
        is Spl -> SplElement(element)
        is Code -> CodeElement(element)
        is Sup -> SupElement(element)
        is Sub -> SubElement(element)
        is Br -> BrElement()
        is Paragraph -> ParagraphElement(element)
        is cn.yurn.yutori.message.element.Message -> MessageElement(element)
        is Quote -> QuoteElement(element)
        is Author -> AuthorElement(element)
        is Button -> ButtonElement(element)
        else -> UnsupportedElement(element)
    }
}