package ar.com.play2play.data.bluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothDeviceFinder {

    fun listDevices(onFound: (devices: List<BluetoothDevice>) -> Unit)
}
