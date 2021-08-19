package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.model.base.message.Message

abstract class BluetoothConnectionImp(protected val handler: Handler) : Thread(), BluetoothConnection {

    private val objectMapper by lazy { jacksonObjectMapper() }

    protected val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    protected fun createConnectionThread(bluetoothSocket: BluetoothSocket): BluetoothConnectionThread {
        return BluetoothConnectionThread(handler, bluetoothSocket)
    }

    protected fun sendMessage(
        messageSenderThread: MessageSenderThread,
        connection: BluetoothConnectionThread,
        message: Message,
        isConversation: Boolean
    ) {
        val messageBytesArray = objectMapper.writeValueAsBytes(message)
        messageSenderThread.putMessage(connection, messageBytesArray, messageBytesArray.size, isConversation)
    }
}
