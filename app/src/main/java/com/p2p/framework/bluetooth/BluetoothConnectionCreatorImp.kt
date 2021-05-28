package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.bluetooth.Message
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_READ
import com.p2p.utils.Logger

class BluetoothConnectionCreatorImp(private val handler: Handler) : BluetoothConnectionCreator {

    override fun createServer() = BluetoothServer(handler)

    override fun createClient(serverDevice: BluetoothDevice) = BluetoothClient(handler, serverDevice)
}
