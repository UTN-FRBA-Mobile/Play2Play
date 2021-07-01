package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_FAILURE
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_SUCCESS
import com.p2p.model.base.message.Conversation
import com.p2p.model.base.message.Message
import com.p2p.utils.Logger
import java.io.IOException
import java.util.UUID

class BluetoothClient(
    handler: Handler,
    private val bluetoothServerDevice: BluetoothDevice,
) : BluetoothConnectionImp(handler) {

    /** This [BluetoothConnectionThread] contains the communication with the server. */
    private var connectionToServer: BluetoothConnectionThread? = null

    init {
        start()
    }

    override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        tryConnection()
    }

    private fun tryConnection(pendingRetries: Int = RETRY_COUNT) {
        bluetoothServerDevice
            .createRfcommSocketToServiceRecord(UUID.fromString(BluetoothServer.UUID))
            ?.let { socket ->
                Logger.d(TAG, "Try connect to the socket: $socket (attempts left: $pendingRetries)")

                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                try {
                    socket.connect()

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    Logger.d(TAG, "Connection succeed")
                    connectionToServer = createConnectionThread(socket)
                    handler.obtainMessage(ON_CLIENT_CONNECTION_SUCCESS).sendToTarget()
                } catch (exception: IOException) {
                    if (pendingRetries > 0) tryConnection(pendingRetries - 1)
                    null
                }
            } ?: handler.obtainMessage(ON_CLIENT_CONNECTION_FAILURE).sendToTarget()
    }

    override fun close() {
        Logger.d(TAG, "Close the client")
        try {
            connectionToServer?.close()
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the client socket", e)
        }
    }

    override fun write(message: Message) = write(message, isConversation = false)

    override fun talk(conversation: Conversation, sendMessage: Message) {
        if (connectionToServer?.id == conversation.peer) {
            write(sendMessage, isConversation = true)
        } else {
            Logger.e(TAG, "Cannot answer to the given sender id")
        }
    }

    private fun write(message: Message, isConversation: Boolean) {
        connectionToServer?.let { writeOnConnection(it, message, isConversation) }
    }

    companion object {

        private const val TAG = "P2P_CLIENT_BLUETOOTH"
        private const val RETRY_COUNT = 5
    }
}
