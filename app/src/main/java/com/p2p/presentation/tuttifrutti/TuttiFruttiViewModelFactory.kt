package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.SharedPreferencesUserInfoStorage
import com.p2p.framework.bluetooth.BluetoothConnectionCreatorImp
import com.p2p.presentation.base.game.ConnectionType

class TuttiFruttiViewModelFactory(
    private val context: Context,
    private val gameConnectionType: String,
    private val device: BluetoothDevice?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(
            ConnectionType::class.java,
            UserSession::class.java,
            BluetoothConnectionCreator::class.java
        )
        .newInstance(
            ConnectionType(gameConnectionType, device),
            UserSession(SharedPreferencesUserInfoStorage(context)),
            BluetoothConnectionCreatorImp(Looper.getMainLooper())
        )
}
