package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.p2p.model.base.message.Conversation
import com.p2p.model.base.message.Message
import com.p2p.utils.Logger
import java.io.IOException

class BluetoothServer(
    handler: Handler,
    private val maxAccepted: Int = MAX_ACCEPTED,
) : BluetoothConnectionImp(handler) {

    private var shouldLoop = true

    /** This list of [BluetoothConnectionThread]s contains the communication with each client. */
    private val connectionsToClients = mutableListOf<BluetoothConnectionThread>()

    /** The server socket is used to accept connections, then it should be closed. */
    private val acceptingConnectionsSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
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
                acceptingConnectionsSocket?.accept()
            } catch (e: IOException) {
                Logger.d(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            } ?: continue

            Logger.d(TAG, "Accepted socket: ${socket.remoteDevice.name}")
            val bluetoothConnectionThread = createConnectionThread(socket).apply {
                onMessageReceived = { isConversation, length, buffer ->
                    if (!isConversation) {
                        Logger.d(TAG, "Received message and broadcasting")
                        connectionsToClients
                            .filterNot { it == this }
                            .forEach { it.write(buffer, length, false) }
                    }
                }
                onConnectionLost = {
                    connectionsToClients.remove(this)
                    handler
                        .obtainMessage(BluetoothHandlerMessages.ON_CLIENT_CONNECTION_LOST, id)
                        .sendToTarget()
                }
            }
            connectionsToClients.add(bluetoothConnectionThread)
            if (connectionsToClients.size >= maxAccepted) {
                stopAccepting()
            }
        }
    }

    override fun close() {
        Logger.d(TAG, "Close the server")
        try {
            acceptingConnectionsSocket?.close()
            connectionsToClients.forEach { it.close() }
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the connect socket", e)
        }
    }

    override fun write(message: Message) = connectionsToClients.forEach {
        writeOnConnection(it, message, isConversation = false)
    }

    override fun talk(conversation: Conversation, sendMessage: Message) {
        connectionsToClients
            .firstOrNull { it.id == conversation.peer }
            ?.let { writeOnConnection(it, sendMessage, isConversation = true) }
    }

    fun stopAccepting() {
        Logger.d(TAG, "Stop accepting new connections")
        acceptingConnectionsSocket?.close()
        shouldLoop = false
    }

    companion object {

        const val UUID = "7584ba60-bcb4-11eb-ba2f-0800200c9a66"

        private const val MAX_ACCEPTED = 6
        private const val TAG = "P2P_SERVER_BLUETOOTH"
    }
}
