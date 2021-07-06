package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import androidx.core.os.bundleOf
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_READ
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_WRITE_ERROR
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_WRITE_SUCCESS
import com.p2p.utils.Logger
import com.p2p.utils.toBoolean
import com.p2p.utils.toByteArray
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.ceil

class BluetoothConnectionThread(
    private val handler: Handler,
    private val socket: BluetoothSocket
) : Thread() {

    var onConnectionLost: (() -> Unit)? = null
    var onMessageReceived: ((isConversation: Boolean, length: Int, buffer: ByteArray) -> Unit)? = null

    private val inputStream: InputStream = socket.inputStream
    private val outputStream: OutputStream = socket.outputStream
    private val buffer: ByteArray = ByteArray(BUFFER_SIZE)

    init {
        start()
    }

    override fun run() {
        // Keep listening to the InputStream until an exception occurs.
        infinityLoop@ while (true) {
            // Read from the InputStream.
            var numBytes = readFromStream() ?: break

            // Send the obtained bytes to the UI activity.
            val isConversation = buffer[IS_CONVERSATION_INDEX].toInt().toBoolean()
            val packages = buffer[PACKAGES_COUNT_INDEX].toInt()
            var byteArray = buffer.copyOfRange(EXTRA_INFO_SIZE, numBytes)
            Logger.d(TAG, "Read 1/$packages message...")
            for (i in 1 until packages) {
                val newNumBytes = readFromStream() ?: break@infinityLoop
                numBytes += newNumBytes
                byteArray += buffer.copyOfRange(0, newNumBytes)
                Logger.d(TAG, "Read ${i + 1}/$packages message...")
            }
            val finalNumBytes = numBytes - EXTRA_INFO_SIZE
            onMessageReceived?.invoke(isConversation, finalNumBytes, byteArray)
            handler
                .obtainMessage(MESSAGE_READ, -1, -1, byteArray)
                .apply { data = bundleOf(PEER_ID to this@BluetoothConnectionThread.id) }
                .sendToTarget()
        }
    }

    private fun readFromStream() = try {
        // This method blocks until input data is available, end of file is detected, or an exception is thrown.
        Logger.d(TAG, "Reading")
        inputStream.read(buffer)
    } catch (e: IOException) {
        Logger.d(TAG, "Input stream was disconnected", e)
        onConnectionLost?.invoke()
        null
    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray, length: Int, isConversation: Boolean) {
        try {
            val packages = ceil((length + EXTRA_INFO_SIZE).toFloat() / MESSAGE_BUFFER_SIZE).toInt()
            val packagesByteArray = packages.toByteArray() ?: throw IOException("We cannot send $packages packages")
            val finalBytes = isConversation.toByteArray() + packagesByteArray + bytes
            val finalBytesLength = length + EXTRA_INFO_SIZE
            for (i in 0 until packages) {
                val offset = i * MAX_BUFFER_SIZE
                val messageLength = (finalBytesLength - offset).coerceAtMost(MAX_BUFFER_SIZE)
                Logger.d(TAG, "Writing ${i + 1}/$packages message...")
                outputStream.write(finalBytes, offset, messageLength)
            }
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

        /**
         * [MAX_BUFFER_SIZE] should be less than the [BUFFER_SIZE] because at read or at write
         * some extra bytes are added (~40B).
         */
        private const val MAX_BUFFER_SIZE = 950
        private const val IS_CONVERSATION_INDEX = 0
        private const val PACKAGES_COUNT_INDEX = 1
        private const val BUFFER_SIZE = 1024
        private const val EXTRA_INFO_SIZE = 2
        private const val MESSAGE_BUFFER_SIZE = MAX_BUFFER_SIZE - EXTRA_INFO_SIZE
    }
}
