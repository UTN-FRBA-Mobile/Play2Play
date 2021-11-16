package ar.com.play2play.presentation.home.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.com.play2play.data.bluetooth.BluetoothDeviceFinder
import ar.com.play2play.framework.bluetooth.BluetoothDeviceFinderImp
import ar.com.play2play.framework.bluetooth.BluetoothDeviceFinderReceiver

class JoinGamesViewModelFactory(private val receiver: BluetoothDeviceFinderReceiver) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(BluetoothDeviceFinder::class.java)
        .newInstance(BluetoothDeviceFinderImp(receiver))
}
