package com.p2p.presentation.home.join

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothDeviceFinder
import com.p2p.presentation.base.BaseViewModel

class JoinGamesViewModel(bluetoothDeviceFinder: BluetoothDeviceFinder) : BaseViewModel<Unit>() {

    private val _devices: MutableLiveData<List<BluetoothDevice>> = MutableLiveData()
    val devices: LiveData<List<BluetoothDevice>> = _devices

    private val _connectButtonEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val connectButtonEnabled: LiveData<Boolean> = _connectButtonEnabled

    private var selectedDevice: BluetoothDevice? = null

    init {
        bluetoothDeviceFinder.listDevices { newDevices ->
            _devices.value = _devices.value?.let { it + newDevices } ?: newDevices
        }
    }

    fun connect() {
        // TODO("Not yet implemented")
    }

    fun selectDevice(device: BluetoothDevice?) {
        _connectButtonEnabled.value = device != null
        selectedDevice = device
    }
}
