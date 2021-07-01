package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.model.base.message.Conversation
import com.p2p.model.base.message.Message
import com.p2p.utils.Logger
import java.io.IOException

class BluetoothServer(
    handler: Handler,
    private val maxAccepted: Int = MAX_ACCEPTED,
) : BluetoothConnectionImp(handler) {

    private var shouldLoop = true
    private val connectedThreads = mutableListOf<BluetoothConnectionThread>()
    private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(TAG, java.util.UUID.fromString(UUID))
    }

    init {
        start()
    }

    override fun run() {
        // Keep listening until exception occurs or a socket is returned.
        while (shouldLoop) {
            val socket: BluetoothSocket = try {
                Logger.d(TAG, "Accepting new connection, blocking this thread")
                serverSocket?.accept()
            } catch (e: IOException) {
                Logger.d(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            } ?: continue

            Logger.d(TAG, "Accepted socket: ${socket.remoteDevice.name}")
            val bluetoothConnectionThread = createConnectionThread(socket)
            bluetoothConnectionThread.onMessageReceived = { isConversation, length, buffer ->
                if (!isConversation) {
                    Logger.d(TAG, "Received message and broadcasting")
                    connectedThreads
                        .filterNot { it == bluetoothConnectionThread }
                        .forEach { it.write(buffer, length, false) }
                }
            }
            connectedThreads.add(bluetoothConnectionThread)
            if (connectedThreads.size >= maxAccepted) {
                stopAccepting()
            }
        }
    }

    override fun close() {
        Logger.d(TAG, "Close the server")
        try {
            serverSocket?.close()
            connectedThreads.forEach { it.close() }
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the connect socket", e)
        }
    }

    override fun write(message: Message) = connectedThreads.forEach {
        writeOnConnection(it, message, isConversation = false)
    }

    override fun talk(conversation: Conversation, sendMessage: Message) {
        connectedThreads
            .firstOrNull { it.id == conversation.peer }
            ?.let { writeOnConnection(it, sendMessage, isConversation = true) }
    }

    fun stopAccepting() {
        Logger.d(TAG, "Stop accepting new connections")
        serverSocket?.close() // TODO: I'm not sure that we should close this socket.
        shouldLoop = false
    }

    companion object {

        const val UUID = "7584ba60-bcb4-11eb-ba2f-0800200c9a66"

        private const val MAX_ACCEPTED = 6
        private const val TAG = "P2P_SERVER_BLUETOOTH"
    }
}
