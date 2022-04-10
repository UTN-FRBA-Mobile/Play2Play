package ar.com.play2play.data.bluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothConnectionCreator {

    fun getMyDeviceName(): String

    fun createServer(): BluetoothConnection

    fun createClient(serverDevice: BluetoothDevice): BluetoothConnection

    fun makeMeVisible()
}
