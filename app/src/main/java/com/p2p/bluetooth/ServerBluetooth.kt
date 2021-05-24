package com.p2p.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.util.Log
import com.p2p.presentation.home.HomeActivity
import java.io.IOException
import java.util.UUID

class ServerBluetooth : Bluetooth() {

    override fun init(activity: HomeActivity) {
        super.init(activity)
        Log.d("DylanLog", "Initialize")
        AcceptThread().start()
    }

    private inner class AcceptThread : Thread() {

        private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            val uuid = UUID.randomUUID()
            Log.d("DylanLog", "UUID: $uuid")
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(TAG, uuid)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                Log.d("DylanLog", "Run")
                val socket: BluetoothSocket? = try {
                    Log.d("DylanLog", "Accept: $serverSocket")
                    serverSocket?.accept()
                } catch (e: IOException) {
                    Log.e("DylanLog", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                Log.d("DylanLog", "Accept success")
                Log.d("DylanLog", "Socket: $socket")
                socket?.also {
                    Log.d("DylanLog", "Server created: ${it.remoteDevice.address}")
                    manageMyConnectedSocket(it)
                    serverSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    companion object {

        private const val REQUEST_ENABLE_BT = 1001
        private const val TAG = "P2P_SERVER_BLUETOOTH"
    }
}