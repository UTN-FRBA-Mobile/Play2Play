package com.p2p.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.p2p.framework.Logger
import com.p2p.presentation.home.HomeActivity
import java.io.IOException
import java.util.UUID

class ClientBluetooth : Bluetooth() {

    private var connectedSocket: BluetoothConnectionThread? = null

    fun init(activity: HomeActivity, bluetoothDevice: BluetoothDevice) {
        init(activity)
        ConnectThread(bluetoothDevice).start()
    }

    inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val serverSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString(ServerBluetooth.UUID))
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            Logger.d(TAG, "Will connect to the server socket: $serverSocket")
            serverSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Logger.d(TAG, "Trying to connect to the server")
                socket.connect() // TODO: try-catch timeout or closed server

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                Logger.d(TAG, "Connection succeed")
                connectedSocket = manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            Logger.d(TAG, "Close the client")
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    override fun write(byteArray: ByteArray, offset: Int, length: Int) {
        connectedSocket?.write(byteArray, offset, length)
    }

    companion object {

        private const val TAG = "P2P_CLIENT_BLUETOOTH"
    }
}