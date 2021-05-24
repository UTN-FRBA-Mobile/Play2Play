package com.p2p.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.util.Log
import com.p2p.presentation.home.HomeActivity
import java.io.IOException
import java.util.UUID

class ClientBluetooth : Bluetooth() {

    fun init(activity: HomeActivity, bluetoothDevice: BluetoothDevice, uuid: String) {
        init(activity)
        ConnectThread(bluetoothDevice, uuid).start()
    }

    inner class ConnectThread(device: BluetoothDevice, uuid: String) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    companion object {

        private const val TAG = "P2P_CLIENT_BLUETOOTH"
    }
}