package ar.com.play2play.presentation.home.join

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.R
import ar.com.play2play.data.bluetooth.BluetoothDeviceFinder
import ar.com.play2play.framework.bluetooth.isPhone
import ar.com.play2play.presentation.base.BaseViewModel

class JoinGamesViewModel(bluetoothDeviceFinder: BluetoothDeviceFinder) : BaseViewModel<JoinGamesEvent>() {

    private val _devices: MutableLiveData<Set<BluetoothDevice>> = MutableLiveData()
    val devices: LiveData<Set<BluetoothDevice>> = _devices

    private val _connectButtonEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val connectButtonEnabled: LiveData<Boolean> = _connectButtonEnabled

    private var selectedDevice: BluetoothDevice? = null

    init {
        bluetoothDeviceFinder.listDevices { newDevicesList ->
            val newDevices = newDevicesList.filter { it.name?.isNotBlank() == true && it.isPhone() }.toSet()
            _devices.value = _devices.value?.let { newDevices + it } ?: newDevices
        }
    }

    fun connect() {
        selectedDevice?.let {
            dispatchSingleTimeEvent(JoinGame(it))
        } ?: run {
            dispatchMessage(textRes = R.string.join_game_select_device, type = MessageData.Type.ERROR)
        }
    }

    fun selectDevice(device: BluetoothDevice?) {
        selectedDevice = device
        _connectButtonEnabled.value = device != null
    }

    fun troubleshoot() = dispatchSingleTimeEvent(GoToHowToConnectBluetooth)
}
