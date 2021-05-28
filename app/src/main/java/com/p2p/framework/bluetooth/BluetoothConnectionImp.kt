package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.Message

abstract class BluetoothConnectionImp(private val handler: Handler) : Thread(), BluetoothConnection {

    private val objectMapper by lazy { ObjectMapper() }

    protected val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    protected fun createConnectionThread(bluetoothSocket: BluetoothSocket): BluetoothConnectionThread {
        return BluetoothConnectionThread(handler, bluetoothSocket)
    }

    protected fun writeOnConnection(connection: BluetoothConnectionThread, message: Message) {
        val messageBytesArray = objectMapper.writeValueAsBytes(message)
        connection.write(messageBytesArray, 0, messageBytesArray.size)
    }
}
