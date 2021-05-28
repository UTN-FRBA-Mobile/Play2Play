package com.p2p.presentation.home.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.p2p.data.bluetooth.BluetoothDeviceFinder
import com.p2p.framework.bluetooth.BluetoothDeviceFinderImp
import com.p2p.framework.bluetooth.BluetoothDeviceFinderReceiver

class JoinGamesViewModelFactory(private val receiver: BluetoothDeviceFinderReceiver) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(BluetoothDeviceFinder::class.java)
        .newInstance(BluetoothDeviceFinderImp(receiver))
}
