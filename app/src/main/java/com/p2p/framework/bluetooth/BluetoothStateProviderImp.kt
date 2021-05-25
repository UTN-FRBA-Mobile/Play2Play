package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothAdapter
import com.p2p.data.bluetooth.BluetoothStateProvider

class BluetoothStateProviderImp : BluetoothStateProvider {

    private val bluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun isEnabled() = bluetoothAdapter.isEnabled
}
