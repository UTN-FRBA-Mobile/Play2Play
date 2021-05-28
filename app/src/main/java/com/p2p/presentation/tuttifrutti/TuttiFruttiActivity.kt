package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.p2p.R
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment

class TuttiFruttiActivity : GameActivity<TuttiFruttiSpecificGameEvent, TuttiFruttiViewModel>() {

    override val viewModel: TuttiFruttiViewModel by gameViewModels()

    override fun goToCreate() = addFragment(CreateTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToPlay() = Unit // TODO: addFragment(CountdownTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToClientLobby() = Unit // TODO: addFragment(ClientLobbyTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToServerLobby() = Unit // TODO: addFragment(ServerLobbyTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    companion object {

        fun startCreate(context: Context) = startCreate(TuttiFruttiActivity::class, context)

        fun startJoin(context: Context, serverDevice: BluetoothDevice) {
            startJoin(TuttiFruttiActivity::class, context, serverDevice)
        }
    }
}
