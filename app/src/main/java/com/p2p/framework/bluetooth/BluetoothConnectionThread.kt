package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import androidx.core.os.bundleOf
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_READ
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_WRITE_ERROR
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_WRITE_SUCCESS
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_LOST
import com.p2p.utils.Logger
import com.p2p.utils.toBoolean
import com.p2p.utils.toByteArray
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothConnectionThread(
    private val handler: Handler,
    private val socket: BluetoothSocket
) : Thread() {

    var onConnectionLost: (() -> Boolean)? = null
    var onMessageReceived: ((isConversation: Boolean, length: Int, buffer: ByteArray) -> Unit)? = null

    private val inputStream: InputStream = socket.inputStream
    private val outputStream: OutputStream = socket.outputStream
    private val buffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

    init {
        start()
    }

    override fun run() {
        var numBytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            // Read from the InputStream.
            numBytes = try {
                // This method blocks until input data is available, end of file is detected, or an exception is thrown.
                Logger.d(TAG, "Reading")
                inputStream.read(buffer)
            } catch (e: IOException) {
                Logger.d(TAG, "Input stream was disconnected", e)
                handler.obtainMessage(ON_CLIENT_CONNECTION_LOST, id).sendToTarget()
                onConnectionLost?.invoke()
                break
            }

            // Send the obtained bytes to the UI activity.
            Logger.d(TAG, "Message arrived")
            val isConversation = buffer[0].toInt().toBoolean()
            val byteArray = buffer.copyOfRange(1, numBytes)
            onMessageReceived?.invoke(isConversation, numBytes - 1, byteArray)
            handler
                .obtainMessage(MESSAGE_READ, -1, -1, byteArray)
                .apply { data = bundleOf(PEER_ID to this@BluetoothConnectionThread.id) }
                .sendToTarget()
        }
    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray, length: Int, isConversation: Boolean) {
        try {
            Logger.d(TAG, "Writing...")
            outputStream.write(isConversation.toByteArray() + bytes, 0, length + 1)
        } catch (e: IOException) {
            Logger.e(TAG, "Error occurred when sending data", e)

            // Send a failure message back to the activity.
            handler
                .obtainMessage(MESSAGE_WRITE_ERROR, length, -1, bytes)
                .sendToTarget()
            return
        }

        // Share the sent message with the UI activity.
        Logger.d(TAG, "Write succeed")
        handler
            .obtainMessage(MESSAGE_WRITE_SUCCESS, length, -1, bytes)
            .apply { data = bundleOf(PEER_ID to this@BluetoothConnectionThread.id) }
            .sendToTarget()
    }

    // Call this method from the main activity to shut down the connection.
    fun close() {
        Logger.d(TAG, "Close the socket #$id")
        try {
            socket.close()
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the connect socket", e)
        }
    }

    companion object {
        const val PEER_ID = "PEER"
        const val TAG = "P2P_BLUETOOTH_SERVICE"
    }
}
