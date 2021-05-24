package com.p2p.bluetooth

import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import com.p2p.framework.Logger
import com.p2p.presentation.home.HomeActivity
import java.io.IOException

class ServerBluetooth : Bluetooth() {

    override fun init(activity: HomeActivity) {
        super.init(activity)
        AcceptThread().start()
    }

    private inner class AcceptThread(private val maxAccepted: Int = MAX_ACCEPTED) : Thread() {

        private var shouldLoop = true
        private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(TAG, java.util.UUID.fromString(UUID))
        }

        private val connectedThreads = mutableListOf<BluetoothConnectionThread>()

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            while (shouldLoop) {
                val socket: BluetoothSocket = try {
                    Logger.d(TAG, "Accepting new connection")
                    serverSocket?.accept()
                } catch (e: IOException) {
                    Logger.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                } ?: continue

                Logger.d(TAG, "Socket: $socket")
                Logger.d(TAG, "Server created: ${socket.remoteDevice.address}")
                val bluetoothConnectionThread = manageMyConnectedSocket(socket)
                connectedThreads.add(bluetoothConnectionThread)
                if (maxAccepted >= connectedThreads.size) {
                    stopAccepting()
                }
            }
        }

        fun stopAccepting() {
            serverSocket?.close()
            shouldLoop = false
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Logger.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    companion object {

        const val UUID = "7584ba60-bcb4-11eb-ba2f-0800200c9a66"

        private const val MAX_ACCEPTED = 6
        private const val TAG = "P2P_SERVER_BLUETOOTH"
    }
}