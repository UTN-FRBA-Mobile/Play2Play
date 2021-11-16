package ar.com.play2play.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import ar.com.play2play.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_FAILURE
import ar.com.play2play.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_SUCCESS
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.utils.Logger
import java.io.IOException
import java.util.UUID

class BluetoothClient(
    handler: Handler,
    private val bluetoothServerDevice: BluetoothDevice,
) : BluetoothConnectionImp(handler) {

    /** This [BluetoothConnectionThread] contains the communication with the server. */
    private var connectionToServer: BluetoothConnectionThread? = null
    private val messageSenderThread = MessageSenderThread()

    private var isClosed = false

    init {
        start()
    }

    override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        val isSuccess = tryConnection()
        handler
            .obtainMessage(if (isSuccess) ON_CLIENT_CONNECTION_SUCCESS else ON_CLIENT_CONNECTION_FAILURE)
            .sendToTarget()
    }

    private fun tryConnection(pendingRetries: Int = RETRY_COUNT): Boolean {
        return try {
            bluetoothServerDevice
                .createRfcommSocketToServiceRecord(UUID.fromString(BluetoothServer.UUID))
                ?.let { socket ->
                    Logger.d(TAG, "Try connect to the socket: $socket (attempts left: $pendingRetries)")

                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    socket.connect()

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    Logger.d(TAG, "Connection succeed")
                    connectionToServer = createConnectionThread(socket).apply {
                        onConnectionLost = {
                            handler
                                .obtainMessage(BluetoothHandlerMessages.ON_SERVER_CONNECTION_LOST)
                                .sendToTarget()
                        }
                    }
                    true
                } ?: false
        } catch (exception: IOException) {
            if (!isClosed && pendingRetries > 0) {
                sleep(CONNECTION_TIMEOUT)
                tryConnection(pendingRetries - 1)
            } else {
                false
            }
        }
    }

    override fun close() {
        Logger.d(TAG, "Close the client")
        isClosed = true
        try {
            connectionToServer?.close()
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the client socket", e)
        }
    }

    override fun killPeer(peer: Long) = close()

    override fun write(message: Message) = write(message, isConversation = false)

    override fun talk(conversation: Conversation, sendMessage: Message) {
        if (connectionToServer?.id == conversation.peer) {
            write(sendMessage, isConversation = true)
        } else {
            Logger.e(TAG, "Cannot answer to the given sender id")
        }
    }

    private fun write(message: Message, isConversation: Boolean) {
        connectionToServer?.let { sendMessage(messageSenderThread, it, message, isConversation) }
    }

    companion object {

        private const val TAG = "P2P_CLIENT_BLUETOOTH"
        private const val RETRY_COUNT = 5
        private const val CONNECTION_TIMEOUT: Long = 1_000
    }
}
