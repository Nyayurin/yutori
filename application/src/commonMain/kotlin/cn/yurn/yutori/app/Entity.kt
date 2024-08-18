package cn.yurn.yutori.app

data class Chat(
    val id: String,
    val avatar: String,
    val name: String,
    val content: String,
    val updateTime: Long = 0,
    val unread: Boolean = false
)