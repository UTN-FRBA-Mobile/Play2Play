package ar.com.play2play.model.base.message

data class Conversation(
    val lastMessage: Message,
    val peer: Long
)
