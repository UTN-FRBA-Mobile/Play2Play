package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import com.p2p.utils.Logger
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothConnectionThread(
    private val handler: Handler,
    private val socket: BluetoothSocket
) : Thread() {

    var onMessageReceived: ((length: Int, buffer: ByteArray) -> Unit)? = null

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
                break
            }

            // Send the obtained bytes to the UI activity.
            Logger.d(TAG, "Message arrived")
            onMessageReceived?.invoke(numBytes, buffer)
            handler
                .obtainMessage(MESSAGE_READ, numBytes, -1, buffer)
                .sendToTarget()
        }
    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray, offset: Int, length: Int) {
        try {
            Logger.d(TAG, "Writing...")
            outputStream.write(bytes, offset, length)
        } catch (e: IOException) {
            Logger.e(TAG, "Error occurred when sending data", e)

            // Send a failure message back to the activity.
            handler
                .obtainMessage(MESSAGE_WRITE_ERROR)
                .sendToTarget()
            return
        }

        // Share the sent message with the UI activity.
        Logger.d(TAG, "Write succeed")
        handler
            .obtainMessage(MESSAGE_WRITE_SUCCESS, length, -1, bytes)
            .sendToTarget()
    }

    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
            socket.close()
        } catch (e: IOException) {
            Logger.e(TAG, "Could not close the connect socket", e)
        }
    }

    companion object {

        const val MESSAGE_READ: Int = 0
        const val MESSAGE_WRITE_SUCCESS: Int = 1
        const val MESSAGE_WRITE_ERROR: Int = 2
        const val TAG = "P2P_BLUETOOTH_SERVICE"
    }
}
