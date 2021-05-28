package com.p2p.presentation.basegame

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.InstructionsLocalResourcesSource
import com.p2p.framework.SharedPreferencesUserInfoStorage
import com.p2p.framework.bluetooth.BluetoothConnectionCreatorImp

open class GameViewModelFactory(
    private val context: Context,
    private val data: Data
) : ViewModelProvider.Factory {

    protected val connectionType: ConnectionType
        get() = ConnectionType(data.gameConnectionType, data.device)

    protected val userSession: UserSession
        get() = UserSession(SharedPreferencesUserInfoStorage(context))

    protected val bluetoothConnectionCreator: BluetoothConnectionCreator
        get() = BluetoothConnectionCreatorImp(data.handler)

    protected val instructionsRepository: InstructionsRepository
        get() = InstructionsRepository(InstructionsLocalResourcesSource(context))

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(
            ConnectionType::class.java,
            UserSession::class.java,
            BluetoothConnectionCreator::class.java,
            InstructionsRepository::class.java,
        )
        .newInstance(
            connectionType,
            userSession,
            bluetoothConnectionCreator,
            instructionsRepository
        )

    data class Data(
        val handler: Handler,
        val gameConnectionType: String,
        val device: BluetoothDevice?
    )
}
