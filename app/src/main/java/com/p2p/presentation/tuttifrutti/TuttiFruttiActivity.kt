package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.p2p.R
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.basegame.GoToClientLobby
import com.p2p.presentation.basegame.GoToServerLobby
import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment

class TuttiFruttiActivity : GameActivity<TuttiFruttiViewModel>() {

    override val viewModel: TuttiFruttiViewModel by gameViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.players.observe(this) { findViewById<TextView>(R.id.players).text = it.joinToString() }
    }

    override fun onEvent(event: GameEvent) = when (event) {
        GoToClientLobby -> Unit // TODO()
        GoToServerLobby -> Unit // TODO()
        is SpecificGameEvent -> addFragment(CreateTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)
        else -> Unit // Do nothing, it's not necessary to be exhaustive on game events
    }

    companion object {

        fun startCreate(context: Context) = startCreate(TuttiFruttiActivity::class, context)

        fun startJoin(context: Context, serverDevice: BluetoothDevice) {
            startJoin(TuttiFruttiActivity::class, context, serverDevice)
        }
    }
}
