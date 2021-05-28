package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import com.p2p.data.bluetooth.BluetoothConnectionCreator

class BluetoothConnectionCreatorImp(private val handler: Handler) : BluetoothConnectionCreator {

    override fun createServer() = BluetoothServer(handler)

    override fun createClient(serverDevice: BluetoothDevice) = BluetoothClient(handler, serverDevice)
}
