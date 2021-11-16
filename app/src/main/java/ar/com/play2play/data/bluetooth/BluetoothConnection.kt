package ar.com.play2play.data.bluetooth

import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.base.message.Message

interface BluetoothConnection {

    /** Writing on a bluetooth connection implies writing a message that will be sent to all the room. */
    fun write(message: Message)

    /** Talking implies sending a message **only** to the conversation with the [Conversation.peer]. */
    fun talk(conversation: Conversation, sendMessage: Message)

    /** Close the bluetooth connection when there won't be more communication. */
    fun close()

    /** Kills the connection with the given [peer] id. */
    fun killPeer(peer: Long)
}
