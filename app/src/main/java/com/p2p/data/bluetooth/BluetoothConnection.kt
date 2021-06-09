package com.p2p.data.bluetooth

import com.p2p.model.base.message.ConversationMessage
import com.p2p.model.base.message.Message

interface BluetoothConnection {

    /** Writing on a bluetooth connection implies writing a message that will be sent to all the room. */
    fun write(message: Message)

    /** Talking implies sending a message **only** to the conversation with the [ConversationMessage.peer]. */
    fun talk(conversationMessage: ConversationMessage, sendMessage: Message)

    /** Runs an action when the connection to the room is established. */
    fun onConnected(action: (BluetoothConnection) -> Unit)

    /** Close the bluetooth connection when there won't be more communication. */
    fun close()
}
