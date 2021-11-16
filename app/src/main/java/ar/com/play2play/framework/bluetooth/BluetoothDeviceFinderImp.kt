package ar.com.play2play.framework.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import ar.com.play2play.data.bluetooth.BluetoothDeviceFinder

class BluetoothDeviceFinderImp(private val receiver: BluetoothDeviceFinderReceiver) : BluetoothDeviceFinder {

    private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun listDevices(onFound: (devices: List<BluetoothDevice>) -> Unit) {

        // Search the available devices
        receiver.onFound = onFound
        bluetoothAdapter?.cancelDiscovery()
        bluetoothAdapter?.startDiscovery()

        // Get the already bonded devices
        bluetoothAdapter?.bondedDevices?.let { devices -> onFound(devices.toList()) }
    }
}
