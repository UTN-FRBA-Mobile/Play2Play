package com.p2p.data.bluetooth

import com.p2p.model.message.Message
import com.p2p.model.message.MessageReceived

interface BluetoothConnection {

    fun write(message: Message)

    fun answer(messageReceived: MessageReceived, sendMessage: Message)

    fun onConnected(action: (BluetoothConnection) -> Unit)

    fun close()
}
