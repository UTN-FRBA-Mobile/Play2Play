package com.p2p.presentation.home.join

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.R
import com.p2p.data.bluetooth.BluetoothDeviceFinder
import com.p2p.presentation.base.BaseViewModel

class JoinGamesViewModel(bluetoothDeviceFinder: BluetoothDeviceFinder) : BaseViewModel<JoinGamesEvent>() {

    private val _devices: MutableLiveData<Set<BluetoothDevice>> = MutableLiveData()
    val devices: LiveData<Set<BluetoothDevice>> = _devices

    private val _connectButtonEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val connectButtonEnabled: LiveData<Boolean> = _connectButtonEnabled

    private var selectedDevice: BluetoothDevice? = null

    init {
        bluetoothDeviceFinder.listDevices { newDevices ->
            _devices.value = _devices.value?.let { it + newDevices } ?: newDevices.toSet()
        }
    }

    fun connect() {
        selectedDevice?.let {
            dispatchSingleTimeEvent(JoinGame(it))
        } ?: run {
            dispatchMessage(MessageData(R.string.join_game_select_device, type = MessageData.Type.ERROR))
        }
    }

    fun selectDevice(device: BluetoothDevice?) {
        selectedDevice = device
        _connectButtonEnabled.value = device != null
    }

    fun troubleshoot() = dispatchSingleTimeEvent(GoToHowToConnectBluetooth)
}
