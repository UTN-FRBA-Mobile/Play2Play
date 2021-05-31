package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.framework.InstructionsLocalResourcesSource
import com.p2p.presentation.base.BaseMVVMActivity
import com.p2p.presentation.base.game.*
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment

class TuttiFruttiActivity : BaseMVVMActivity<GameEvent, TuttiFruttiViewModel>() {

    private val gameConnectionType: String by lazy {
        intent.getStringExtra(GameConnectionType.EXTRA) ?: "UNKNOWN"
    }
    private val device: BluetoothDevice? by lazy { intent.getParcelableExtra(SERVER_DEVICE_EXTRA) }

    override val viewModel: TuttiFruttiViewModel by viewModels {
        TuttiFruttiViewModelFactory(baseContext, gameConnectionType, device)
    }

    private val instructions by lazy {
        InstructionsRepository(InstructionsLocalResourcesSource(applicationContext)).getInstructions(
            Game.TUTTI_FRUTTI
        )
    }

    override fun onEvent(event: GameEvent) = when (event) {
        GoToClientLobby -> Unit // TODO()
        GoToServerLobby -> Unit // TODO()
        is SpecificGameEvent -> addFragment(
            CreateTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false
        )
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
