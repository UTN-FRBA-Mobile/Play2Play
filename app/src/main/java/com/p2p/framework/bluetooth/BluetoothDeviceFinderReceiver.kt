package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * The broadcast receiver is used to be informed about the requested device's events.
 *
 * In this case we are registering it on the fragment with the requested event: [BluetoothDevice.ACTION_FOUND]
 * that will inform us when a bluetooth device is found.
 *
 * If you want to see how to find bluetooth devices, see [BluetoothDeviceFinderImp].
 */
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
