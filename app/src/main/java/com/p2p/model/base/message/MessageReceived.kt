package com.p2p.model.base.message

data class MessageReceived(
    val message: Message,
    val senderId: Long
)
