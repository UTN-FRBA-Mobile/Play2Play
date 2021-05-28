package com.p2p.data.bluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothDeviceFinder {

    fun listDevices(onFound: (devices: List<BluetoothDevice>) -> Unit)
}
