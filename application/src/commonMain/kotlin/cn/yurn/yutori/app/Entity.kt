package cn.yurn.yutori.app

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String,
    val avatar: String,
    val name: String,
    val content: String,
    val updateTime: Long = 0,
    val unread: Boolean = false
)