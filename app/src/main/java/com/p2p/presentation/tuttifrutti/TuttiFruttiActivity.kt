package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.activity.viewModels
import com.p2p.model.GameInfo
import com.p2p.model.tuttifrutti.TuttiFruttiInfo
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.tuttifrutti.countdown.TuttiFruttiCountdownFragment
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment

class TuttiFruttiActivity : GameActivity<TuttiFruttiSpecificGameEvent, TuttiFruttiViewModel>() {

    override val viewModel: TuttiFruttiViewModel by viewModels { gameViewModelFactory }

    override fun goToCreate() = addFragment(CreateTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToPlay(gameInfo: GameInfo) =
        addFragment(TuttiFruttiCountdownFragment.newInstance(gameInfo as TuttiFruttiInfo),
            shouldAddToBackStack = false)

    override fun goToClientLobby() =
        Unit // TODO: addFragment(ClientLobbyTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToServerLobby() =
        Unit // TODO: addFragment(ServerLobbyTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    companion object {

        fun startCreate(context: Context) = startCreate(TuttiFruttiActivity::class, context)

        fun startJoin(context: Context, serverDevice: BluetoothDevice) {
            startJoin(TuttiFruttiActivity::class, context, serverDevice)
        }
    }
}
