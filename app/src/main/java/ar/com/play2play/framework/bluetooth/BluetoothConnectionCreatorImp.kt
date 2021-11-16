package ar.com.play2play.framework.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Handler
import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator

class BluetoothConnectionCreatorImp(
    private val activity: Activity,
    private val handler: Handler
) : BluetoothConnectionCreator {

    override fun getMyDeviceName() = BluetoothAdapter.getDefaultAdapter().name ?: run {
        BluetoothAdapter.getDefaultAdapter().name = DEFAULT_NAME
        DEFAULT_NAME
    }

    override fun createServer() = BluetoothServer(handler)

    override fun createClient(serverDevice: BluetoothDevice) = BluetoothClient(handler, serverDevice)

    override fun makeMeVisible() {
        activity.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX_DISCOVERABLE_DURATION_SEC)
        })
    }

    companion object {

        private const val DEFAULT_NAME = "Play2Play"
        private const val MAX_DISCOVERABLE_DURATION_SEC = 300
    }
}
