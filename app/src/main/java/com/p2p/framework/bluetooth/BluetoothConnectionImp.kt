package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.fasterxml.jackson.databind.ObjectMapper
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.Message

abstract class BluetoothConnectionImp(private val handler: Handler) : Thread(), BluetoothConnection {

    private val objectMapper by lazy { ObjectMapper() }

    protected val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    protected fun manageMyConnectedSocket(bluetoothSocket: BluetoothSocket): BluetoothConnectionThread {
        return BluetoothConnectionThread(handler, bluetoothSocket)
    }

    protected fun writeOnConnection(connection: BluetoothConnectionThread, message: Message) {
        val messageJson = objectMapper.writeValueAsBytes(message)
        connection.write(messageJson, 0, messageJson.size)
    }
}
