package com.p2p.model.message

data class MessageReceived(
    val message: Message,
    val senderId: Long
)
