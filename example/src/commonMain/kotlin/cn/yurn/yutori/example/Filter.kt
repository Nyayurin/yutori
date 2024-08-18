package cn.yurn.yutori.example

import cn.yurn.yutori.Event
import cn.yurn.yutori.MessageEvent
import cn.yurn.yutori.user

val qqHelperFilter = { event: Event<MessageEvent> -> event.platform == "chronocat" && event.user.id == "2854196310" }