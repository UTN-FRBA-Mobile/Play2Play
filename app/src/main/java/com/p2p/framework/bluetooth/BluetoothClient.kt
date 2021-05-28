package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.model.message.Message
import com.p2p.model.message.MessageReceived
import com.p2p.utils.Logger
import java.io.IOException
import java.util.UUID

class BluetoothClient(
    handler: Handler,
    private val bluetoothServerDevice: BluetoothDevice,
) : BluetoothConnectionImp(handler) {

    private val onConnectedActions = mutableListOf<(BluetoothConnection) -> Unit>()
    private val connectionSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothServerDevice.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothServer.UUID))
    }
    private var connectedSocket: BluetoothConnectionThread? = null

    init {
        start()
    }

    override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        tryConnection()
    }

    private fun tryConnection(pendingRetries: Int = RETRY_COUNT) {
        Logger.d(TAG, "Try connect to the socket: $connectionSocket (attempts left: $pendingRetries)")
        connectionSocket?.let { socket ->
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            try {
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                Logger.d(TAG, "Connection succeed")
                connectedSocket = createConnectionThread(socket)
                onConnectedActions.forEach { it(this) }
                onConnectedActions.clear()
            } catch (exception: IOException) {
                if (pendingRetries > 0) tryConnection(pendingRetries - 1)
            }
        }
    }

    override fun close() {
        Logger.d(TAG, "Close the client")
        try {
            connectionSocket?.close()
            connectedSocket?.close()
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the client socket", e)
        }
    }

    override fun onConnected(action: (BluetoothConnection) -> Unit) {
        if (connectedSocket != null) {
            action(this)
        } else {
            onConnectedActions.add(action)
        }
    }

    override fun write(message: Message) {
        connectedSocket?.let { writeOnConnection(it, message) }
    }

    override fun answer(messageReceived: MessageReceived, sendMessage: Message) {
        if (connectedSocket?.id == messageReceived.senderId) {
            write(sendMessage)
        } else {
            Logger.e(TAG, "Cannot answer to the given sender id")
        }
    }

    companion object {

        private const val TAG = "P2P_CLIENT_BLUETOOTH"
        private const val RETRY_COUNT = 5
    }
}
