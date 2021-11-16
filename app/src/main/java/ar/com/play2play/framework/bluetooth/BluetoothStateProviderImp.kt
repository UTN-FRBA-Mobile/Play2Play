package ar.com.play2play.framework.bluetooth

import android.bluetooth.BluetoothAdapter
import ar.com.play2play.data.bluetooth.BluetoothStateProvider

class BluetoothStateProviderImp : BluetoothStateProvider {

    private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun isEnabled() = bluetoothAdapter.isEnabled
}
