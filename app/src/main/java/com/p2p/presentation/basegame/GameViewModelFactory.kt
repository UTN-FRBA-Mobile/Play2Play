package com.p2p.presentation.basegame

import android.bluetooth.BluetoothDevice
import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.InstructionsLocalResourcesSource
import com.p2p.framework.LoadingTextLocalResourcesSource
import com.p2p.framework.SharedPreferencesUserInfoStorage
import com.p2p.framework.bluetooth.BluetoothConnectionCreatorImp

open class GameViewModelFactory(
    private val activity: GameActivity<*, *>,
    protected val data: Data
) : ViewModelProvider.Factory {

    protected val connectionType: ConnectionType
        get() = ConnectionType(data.gameConnectionType, data.device)

    protected val userSession: UserSession
        get() = UserSession(SharedPreferencesUserInfoStorage(activity.baseContext))

    protected val bluetoothConnectionCreator: BluetoothConnectionCreator
        get() = BluetoothConnectionCreatorImp(activity, data.handler)

    protected val instructionsRepository: InstructionsRepository
        get() = InstructionsRepository(InstructionsLocalResourcesSource(activity.baseContext))

    private val loadingTextRepository: LoadingTextRepository
        get() = LoadingTextRepository(LoadingTextLocalResourcesSource(activity.baseContext))

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(
            ConnectionType::class.java,
            UserSession::class.java,
            BluetoothConnectionCreator::class.java,
            InstructionsRepository::class.java,
            LoadingTextRepository::class.java,
        )
        .newInstance(
            connectionType,
            userSession,
            bluetoothConnectionCreator,
            instructionsRepository,
            loadingTextRepository
        )

    data class Data(
        val handler: Handler,
        val gameConnectionType: String,
        val device: BluetoothDevice?
    )
}
