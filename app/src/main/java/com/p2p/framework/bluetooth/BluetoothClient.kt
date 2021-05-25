package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.Message
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

        Logger.d(TAG, "Try connect to the socket: $connectionSocket")
        tryConnection()
    }

    private fun tryConnection(pendingRetries: Int = RETRY_COUNT) {
        connectionSocket?.let { socket ->
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            try {
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                Logger.d(TAG, "Connection succeed")
                connectedSocket = manageMyConnectedSocket(socket)
                onConnectedActions.forEach { it(this) }
                onConnectedActions.clear()
            } catch (exception: IOException) {
                if (pendingRetries > 0) tryConnection(pendingRetries - 1)
            }
        }
    }

    // Closes the client socket and causes the thread to finish.
    fun close() {
        Logger.d(TAG, "Close the client")
        try {
            connectionSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    fun onConnected(action: (BluetoothConnection) -> Unit) {
        if (connectedSocket != null) {
            action(this)
        } else {
            onConnectedActions.add(action)
        }
    }

    override fun write(message: Message) {
        connectedSocket?.let { writeOnConnection(it, message) }
    }

    companion object {

        private const val TAG = "P2P_CLIENT_BLUETOOTH"
        private const val RETRY_COUNT = 5
    }
}
