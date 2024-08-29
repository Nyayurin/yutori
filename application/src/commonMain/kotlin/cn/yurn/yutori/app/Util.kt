package cn.yurn.yutori.app

import cn.yurn.yutori.app.ui.components.AtMessageElementViewer
import cn.yurn.yutori.app.ui.components.AudioMessageElementViewer
import cn.yurn.yutori.app.ui.components.AuthorMessageElementViewer
import cn.yurn.yutori.app.ui.components.BoldMessageElementViewer
import cn.yurn.yutori.app.ui.components.BrMessageElementViewer
import cn.yurn.yutori.app.ui.components.ButtonMessageElementViewer
import cn.yurn.yutori.app.ui.components.CodeMessageElementViewer
import cn.yurn.yutori.app.ui.components.DeleteMessageElementViewer
import cn.yurn.yutori.app.ui.components.EmMessageElementViewer
import cn.yurn.yutori.app.ui.components.FileMessageElementViewer
import cn.yurn.yutori.app.ui.components.HrefMessageElementViewer
import cn.yurn.yutori.app.ui.components.IdiomaticMessageElementViewer
import cn.yurn.yutori.app.ui.components.ImageMessageElementViewer
import cn.yurn.yutori.app.ui.components.InsMessageElementViewer
import cn.yurn.yutori.app.ui.components.MessageMessageElementViewer
import cn.yurn.yutori.app.ui.components.ParagraphMessageElementViewer
import cn.yurn.yutori.app.ui.components.QuoteMessageElementViewer
import cn.yurn.yutori.app.ui.components.SharpMessageElementViewer
import cn.yurn.yutori.app.ui.components.SplMessageElementViewer
import cn.yurn.yutori.app.ui.components.StrikethroughMessageElementViewer
import cn.yurn.yutori.app.ui.components.StrongMessageElementViewer
import cn.yurn.yutori.app.ui.components.SubMessageElementViewer
import cn.yurn.yutori.app.ui.components.SupMessageElementViewer
import cn.yurn.yutori.app.ui.components.TextMessageElementViewer
import cn.yurn.yutori.app.ui.components.UnderlineMessageElementViewer
import cn.yurn.yutori.app.ui.components.UnsupportedMessageElementViewer
import cn.yurn.yutori.app.ui.components.VideoMessageElementViewer
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
import cn.yurn.yutori.message.element.Text
import cn.yurn.yutori.message.element.Underline
import cn.yurn.yutori.message.element.Video

fun previewMessageContent(content: List<MessageElement>) = buildString {
    for (element in content) {
        append(
            when (element) {
                is Text -> TextMessageElementViewer.preview(element)
                is At -> AtMessageElementViewer.preview(element)
                is Sharp -> SharpMessageElementViewer.preview(element)
                is Href -> HrefMessageElementViewer.preview(element)
                is Image -> ImageMessageElementViewer.preview(element)
                is Audio -> AudioMessageElementViewer.preview(element)
                is Video -> VideoMessageElementViewer.preview(element)
                is File -> FileMessageElementViewer.preview(element)
                is Bold -> BoldMessageElementViewer.preview(element)
                is Strong -> StrongMessageElementViewer.preview(element)
                is Idiomatic -> IdiomaticMessageElementViewer.preview(element)
                is Em -> EmMessageElementViewer.preview(element)
                is Underline -> UnderlineMessageElementViewer.preview(element)
                is Ins -> InsMessageElementViewer.preview(element)
                is Strikethrough -> StrikethroughMessageElementViewer.preview(element)
                is Delete -> DeleteMessageElementViewer.preview(element)
                is Spl -> SplMessageElementViewer.preview(element)
                is Code -> CodeMessageElementViewer.preview(element)
                is Sup -> SupMessageElementViewer.preview(element)
                is Sub -> SubMessageElementViewer.preview(element)
                is Br -> BrMessageElementViewer.preview(element)
                is Paragraph -> ParagraphMessageElementViewer.preview(element)
                is cn.yurn.yutori.message.element.Message -> MessageMessageElementViewer.preview(element)
                is Quote -> QuoteMessageElementViewer.preview(element)
                is Author -> AuthorMessageElementViewer.preview(element)
                is Button -> ButtonMessageElementViewer.preview(element)
                else -> UnsupportedMessageElementViewer.preview(element)
            }
        )
    }
}