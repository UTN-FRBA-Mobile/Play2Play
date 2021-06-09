package com.p2p.data.bluetooth

import com.p2p.model.base.message.Conversation
import com.p2p.model.base.message.Message

interface BluetoothConnection {

    /** Writing on a bluetooth connection implies writing a message that will be sent to all the room. */
    fun write(message: Message)

    /** Talking implies sending a message **only** to the conversation with the [Conversation.peer]. */
    fun talk(conversation: Conversation, sendMessage: Message)

    /** Runs an action when the connection to the room is established. */
    fun onConnected(action: (BluetoothConnection) -> Unit)

    /** Close the bluetooth connection when there won't be more communication. */
    fun close()
}
