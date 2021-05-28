package com.p2p.data.bluetooth

interface BluetoothConnection {

    fun write(message: Message)

    fun onConnected(action: (BluetoothConnection) -> Unit)
}
