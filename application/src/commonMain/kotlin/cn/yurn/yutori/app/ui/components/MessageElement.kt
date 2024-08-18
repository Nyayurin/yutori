package cn.yurn.yutori.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.yurn.yutori.app.MainViewModel
import cn.yurn.yutori.decode
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
import cn.yurn.yutori.message.element.NodeMessageElement
import cn.yurn.yutori.message.element.Paragraph
import cn.yurn.yutori.message.element.Quote
import cn.yurn.yutori.message.element.Sharp
import cn.yurn.yutori.message.element.Spl
import cn.yurn.yutori.message.element.Strikethrough
import cn.yurn.yutori.message.element.Strong
import cn.yurn.yutori.message.element.Sub
import cn.yurn.yutori.message.element.Sup
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun TextElement(element: Text, modifier: Modifier = Modifier) {
    Text(
        text = element.text.decode(),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun AtElement(element: At, modifier: Modifier = Modifier) {
    Text(
        text = "@${element.name}",
        color = MaterialTheme.colorScheme.inversePrimary,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun SharpElement(element: Sharp, modifier: Modifier = Modifier) {
    Text(
        text = "#${element.name}",
        color = MaterialTheme.colorScheme.inversePrimary,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun HrefElement(element: Href, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        color = MaterialTheme.colorScheme.inversePrimary,
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun ImageElement(element: Image, modifier: Modifier = Modifier) {
    val viewModel = viewModel<MainViewModel>()
    val localDensity = LocalDensity.current
    val screenWidth = localDensity.run { viewModel.screen.width.toPx() }
    val screenHeight = localDensity.run { viewModel.screen.height.toPx() }
    val context = LocalPlatformContext.current
    var size by remember { mutableStateOf<DpSize?>(null) }
    val scope = rememberCoroutineScope()
    scope.launch {
        ImageLoader(context).execute(
            ImageRequest.Builder(context)
                .data(element.src)
                .build()
        ).image?.let { image ->
            val constWidth = 600
            val constHeight = 720
            val maxWidth = ((screenWidth / 1080F) * constWidth).roundToInt()
            val maxHeight = ((screenHeight / 1920F) * constHeight).roundToInt()
            val ratio = min(maxWidth / image.width.toDouble(), maxHeight / image.height.toDouble())
            size = DpSize(
                localDensity.run { (ratio * image.width).roundToInt().toDp() },
                localDensity.run { (ratio * image.height).roundToInt().toDp() }
            )
        }
    }
    if (size != null) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(element.src)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(8.dp)).size(size!!)
        )
    }
}

@Composable
fun AudioElement(element: Audio, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun VideoElement(element: Video, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun FileElement(element: File, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun BoldElement(element: Bold, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun StrongElement(element: Strong, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun IdiomaticElement(element: Idiomatic, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        fontStyle = FontStyle.Italic,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun EmElement(element: Em, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        fontStyle = FontStyle.Italic,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun UnderlineElement(element: Underline, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun InsElement(element: Ins, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun StrikethroughElement(element: Strikethrough, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        textDecoration = TextDecoration.LineThrough,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun DeleteElement(element: Delete, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        textDecoration = TextDecoration.LineThrough,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun SplElement(element: Spl, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun CodeElement(element: Code, modifier: Modifier = Modifier) {
    Text(
        text = "`$element`", style = MaterialTheme.typography.bodyLarge, modifier = modifier
    )
}

@Composable
fun SupElement(element: Sup, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun SubElement(element: Sub, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun BrElement(modifier: Modifier = Modifier) {
    Text(text = "", modifier = modifier)
}

@Composable
fun ParagraphElement(element: Paragraph, modifier: Modifier = Modifier) {
    Text(
        text = element.children.joinToString(""),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun MessageElement(element: MessageElement, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun QuoteElement(element: Quote, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0, 0, 0, 50),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
    ) {
        Column(modifier = modifier.padding(8.dp, 4.dp)) {
            for (child in element.children) when (child) {
                is Text -> Text(
                    text = child.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                    modifier = Modifier.padding(0.dp, 4.dp)
                )

                is At -> Text(
                    text = "@${child.name}",
                    color = MaterialTheme.colorScheme.inversePrimary,
                    style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                    modifier = modifier.padding(0.dp, 4.dp)
                )

                is Image -> Text(
                    text = "[图片]",
                    style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                    modifier = Modifier.padding(0.dp, 4.dp)
                )

                is Quote -> {}
                is Author -> Text(
                    text = child.name ?: "null",
                    style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                    modifier = Modifier.padding(0.dp, 4.dp)
                )

                else -> Text(
                    text = child.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
                    modifier = Modifier.padding(0.dp, 4.dp)
                )
            }
        }
    }
}

@Composable
fun AuthorElement(element: Author, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun ButtonElement(element: Button, modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}

@Composable
fun UnsupportedElement(
    element: MessageElement,
    modifier: Modifier = Modifier
) {
    if (element is NodeMessageElement) {
        if (element.children.size > 0) {
            for (child in element.children) {
                when (child) {
                    is Text -> TextElement(child)
                    is At -> AtElement(child)
                    is Sharp -> SharpElement(child)
                    is Href -> HrefElement(child)
                    is Image -> ImageElement(child)
                    is Audio -> AudioElement(child)
                    is Video -> VideoElement(child)
                    is File -> FileElement(child)
                    is Bold -> BoldElement(child)
                    is Strong -> StrongElement(child)
                    is Idiomatic -> IdiomaticElement(child)
                    is Em -> EmElement(child)
                    is Underline -> UnderlineElement(child)
                    is Ins -> InsElement(child)
                    is Strikethrough -> StrikethroughElement(child)
                    is Delete -> DeleteElement(child)
                    is Spl -> SplElement(child)
                    is Code -> CodeElement(child)
                    is Sup -> SupElement(child)
                    is Sub -> SubElement(child)
                    is Br -> BrElement()
                    is Paragraph -> ParagraphElement(child)
                    is cn.yurn.yutori.message.element.Message -> MessageElement(child)
                    is Quote -> QuoteElement(child)
                    is Author -> AuthorElement(child)
                    is Button -> ButtonElement(child)
                    else -> UnsupportedElement(child)
                }
            }
            return
        }
    }
    Text(
        text = "Unsupported element: $element",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}