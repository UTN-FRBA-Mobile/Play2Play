package com.p2p.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.p2p.framework.Logger
import com.p2p.presentation.home.HomeActivity
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothConnectionThread(
    activity: HomeActivity,
    private val socket: BluetoothSocket,
) : Thread() {

    private val handler: Handler = activity.handler
    private val mmInStream: InputStream = socket.inputStream
    private val mmOutStream: OutputStream = socket.outputStream
    private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

    init {
        activity.connectedThread = this.apply {
            start()
        }
    }

    override fun run() {
        var numBytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            // Read from the InputStream.
            numBytes = try {
                // This method blocks until input data is available, end of file is detected, or an exception is thrown.
                mmInStream.read(mmBuffer)
            } catch (e: IOException) {
                Logger.d(TAG, "Input stream was disconnected", e)
                break
            }

            // Send the obtained bytes to the UI activity.
            handler
                .obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer)
                .sendToTarget()
        }
    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray) {
        try {
            mmOutStream.write(bytes)
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred when sending data", e)

            // Send a failure message back to the activity.
            val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
            val bundle = Bundle().apply {
                putString("toast", "Couldn't send data to the other device")
            }
            writeErrorMsg.data = bundle
            handler.sendMessage(writeErrorMsg)
            return
        }

        // Share the sent message with the UI activity.
        handler
            .obtainMessage(MESSAGE_WRITE, -1, -1, mmBuffer)
            .sendToTarget()
    }

    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
            socket.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }

    companion object {

        const val MESSAGE_READ: Int = 0
        const val MESSAGE_WRITE: Int = 1
        const val MESSAGE_TOAST: Int = 2
        const val TAG = "BLUETOOTH_SERVICE"
    }
}