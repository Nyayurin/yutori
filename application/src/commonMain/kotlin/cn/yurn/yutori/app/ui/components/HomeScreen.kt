package cn.yurn.yutori.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.yurn.yutori.app.Chat
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext

@Composable
fun ChatMenu(chats: List<Chat>, modifier: Modifier = Modifier.fillMaxSize(), onClick: (Chat) -> Unit = {}) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(
            items = chats.distinctBy { it.id },
            key = { chat -> chat.id }
        ) { chat ->
            ChatItem(
                chat = chat,
                onClick = onClick
            )
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: (Chat) -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onClick(chat) },
        modifier = modifier.fillMaxWidth()
    ) {
        BadgedBox(
            badge = {
                if (chat.unread) {
                    Badge()
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Row {
                AsyncImage(
                    model = chat.avatar,
                    contentDescription = null,
                    imageLoader = ImageLoader(LocalPlatformContext.current),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(50.dp)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = chat.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = chat.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}