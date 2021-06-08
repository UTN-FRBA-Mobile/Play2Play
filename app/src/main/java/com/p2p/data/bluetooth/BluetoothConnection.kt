package com.p2p.data.bluetooth

import com.p2p.model.base.message.Message
import com.p2p.model.base.message.MessageReceived

interface BluetoothConnection {

    /** Writing on a bluetooth connection implies writing a message that will be sent to all the room. */
    fun write(message: Message)

    /** Answering implies writing a message that will be sent **only** to the message sender. */
    fun answer(messageReceived: MessageReceived, sendMessage: Message)

    /** Runs an action when the connection to the room is established. */
    fun onConnected(action: (BluetoothConnection) -> Unit)

    /** Close the bluetooth connection when there won't be more communication. */
    fun close()
}
