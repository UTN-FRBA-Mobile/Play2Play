package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothDeviceFinderReceiver : BroadcastReceiver() {

    val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    var onFound: ((devices: List<BluetoothDevice>) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action ?: return
        when (action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                onFound?.invoke(listOf(device))
            }
        }
    }
}
