package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.p2p.presentation.base.BaseMVVMActivity
import com.p2p.presentation.base.game.AbstractGameCreationEvent
import com.p2p.presentation.base.game.GameConnectionType
import com.p2p.presentation.base.game.GoToClientLobby
import com.p2p.presentation.base.game.GoToServerLobby
import com.p2p.presentation.tuttifrutti.create.CreateTuttiFruttiFragment

class TuttiFruttiActivity : BaseMVVMActivity<AbstractGameCreationEvent, TuttiFruttiViewModel>() {

    private val gameConnectionType: String by lazy {
        intent.getStringExtra(GameConnectionType.EXTRA) ?: "UNKNOWN"
    }
    private val device: BluetoothDevice? by lazy { intent.getParcelableExtra(SERVER_DEVICE_EXTRA) }

    override val viewModel: TuttiFruttiViewModel by viewModels {
        TuttiFruttiViewModelFactory(baseContext, gameConnectionType, device)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
        viewModel.startConnection() // TODO: This should be called when the creation is finished, from the Lobby
    }

    override fun onEvent(event: AbstractGameCreationEvent) = when (event) {
        GoToSelectCategories -> addFragment(CreateTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)
        GoToClientLobby -> Unit // TODO()
        GoToServerLobby -> Unit // TODO()
        else -> throw NotImplementedError("An uncaught event was dispatched.")
    }

    companion object {

        private const val SERVER_DEVICE_EXTRA = "SERVER_DEVICE_EXTRA"

        fun startCreate(context: Context) {
            context.startActivity(Intent(context, TuttiFruttiActivity::class.java).apply {
                putExtra(GameConnectionType.EXTRA, GameConnectionType.SERVER)
            })
        }

        fun startJoin(context: Context, serverDevice: BluetoothDevice) {
            context.startActivity(Intent(context, TuttiFruttiActivity::class.java).apply {
                putExtra(GameConnectionType.EXTRA, GameConnectionType.CLIENT)
                putExtra(SERVER_DEVICE_EXTRA, serverDevice)
            })
        }
    }
}
