package ar.com.play2play.presentation.basegame

import android.bluetooth.BluetoothDevice
import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.framework.InstructionsLocalResourcesSource
import ar.com.play2play.framework.LoadingTextLocalResourcesSource
import ar.com.play2play.framework.SharedPreferencesUserInfoStorage
import ar.com.play2play.framework.bluetooth.BluetoothConnectionCreatorImp

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
