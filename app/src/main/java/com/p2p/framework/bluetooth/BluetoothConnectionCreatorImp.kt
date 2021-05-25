package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.bluetooth.Message
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_READ
import com.p2p.utils.Logger

class BluetoothConnectionCreatorImp(looper: Looper) : BluetoothConnectionCreator {

    private val handler = Handler(looper) {
        when (it.what) {
            MESSAGE_READ -> {
                val byteArray = (it.obj as ByteArray).copyOfRange(0, it.arg1)
                val message = jacksonObjectMapper().readValue(byteArray, Message::class.java)
                Logger.d(TAG, "Read: $message")
                true
            }
            else -> false
        }
    }

    override fun createServer() = BluetoothServer(handler)

    override fun createClient(serverDevice: BluetoothDevice) = BluetoothClient(handler, serverDevice)

    companion object {

        private const val TAG = "P2P_BluetoothConnectionCreatorImp"
    }
}
