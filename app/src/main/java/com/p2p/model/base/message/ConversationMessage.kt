package com.p2p.model.base.message

data class ConversationMessage(
    val message: Message,
    val peer: Long
)
