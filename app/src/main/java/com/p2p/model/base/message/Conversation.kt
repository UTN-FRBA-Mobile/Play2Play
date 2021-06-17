package com.p2p.model.base.message

data class Conversation(
    val lastMessage: Message,
    val peer: Long
)
