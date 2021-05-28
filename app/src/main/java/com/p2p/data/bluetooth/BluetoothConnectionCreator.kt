package com.p2p.data.bluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothConnectionCreator {

    fun createServer(): BluetoothConnection

    fun createClient(serverDevice: BluetoothDevice): BluetoothConnection
}
