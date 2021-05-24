package com.p2p.bluetooth

import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import com.p2p.framework.Logger
import com.p2p.presentation.home.HomeActivity
import java.io.IOException

class ServerBluetooth : Bluetooth() {

    private val connectedThreads = mutableListOf<BluetoothConnectionThread>()

    override fun init(activity: HomeActivity) {
        super.init(activity)
        AcceptThread().start()
    }

    private inner class AcceptThread(private val maxAccepted: Int = MAX_ACCEPTED) : Thread() {

        private var shouldLoop = true
        private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(TAG, java.util.UUID.fromString(UUID))
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            while (shouldLoop) {
                val socket: BluetoothSocket = try {
                    Logger.d(TAG, "Accepting new connection, blocking this thread")
                    serverSocket?.accept()
                } catch (e: IOException) {
                    Logger.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                } ?: continue

                Logger.d(TAG, "Accepted socket: ${socket.remoteDevice.name}")
                manageMyConnectedSocket(socket)?.let { bluetoothConnectionThread ->
                    bluetoothConnectionThread.onMessageReceived = { length, buffer ->
                        connectedThreads
                            .filterNot {
                                Logger.d(TAG, "Should remove? $it, ${it == bluetoothConnectionThread}")
                                it == bluetoothConnectionThread
                            }
                            .forEach { it.write(buffer, 0, length) }
                    }
                    connectedThreads.add(bluetoothConnectionThread)
                    if (connectedThreads.size >= maxAccepted) {
                        stopAccepting()
                    }
                } ?: cancel()
            }
        }

        fun stopAccepting() {
            Logger.d(TAG, "Stop accepting new connections")
            serverSocket?.close()
            shouldLoop = false
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            Logger.d(TAG, "Close the server")
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Logger.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    override fun write(byteArray: ByteArray, offset: Int, length: Int) {
        connectedThreads.forEach { it.write(byteArray, offset, length) }
    }

    companion object {

        const val UUID = "7584ba60-bcb4-11eb-ba2f-0800200c9a66"

        private const val MAX_ACCEPTED = 6
        private const val TAG = "P2P_SERVER_BLUETOOTH"
    }
}