package com.p2p.framework.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Handler
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.BluetoothConnectionCreator

class BluetoothConnectionCreatorImp(
    private val activity: Activity,
    private val handler: Handler
) : BluetoothConnectionCreator {

    override fun createServer(): BluetoothConnection {
        activity.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX_DISCOVERABLE_DURATION_SEC)
        })
        return BluetoothServer(handler)
    }

    override fun createClient(serverDevice: BluetoothDevice) = BluetoothClient(handler, serverDevice)

    companion object {
        private const val MAX_DISCOVERABLE_DURATION_SEC = 300
    }
}
