package com.p2p.presentation.truco

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.truco.lobby.ServerTrucoLobbyFragment
import com.p2p.presentation.truco.lobby.TrucoClientLobbyFragment

class TrucoActivity : GameActivity<TrucoSpecificGameEvent, TrucoViewModel>() {

    override val viewModel: TrucoViewModel by viewModels {
        TrucoViewModelFactory(this, gameViewModelFactoryData)
    }

    override fun goToCreate() =
        addFragment(ServerTrucoLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToPlay() {
        viewModel.stopLoading()
        //TODO it could be for 4
        addFragment(TrucoPlayFor2Fragment.newInstance(), shouldAddToBackStack = false)
    }

    override fun goToClientLobby() =
        addFragment(TrucoClientLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToServerLobby() =
        addFragment(ServerTrucoLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun onGameEvent(event: TrucoSpecificGameEvent) {
        super.onGameEvent(event)
        when (event) {
            // TODO: Handle truco events
        }
    }

    companion object {

        fun startCreate(activity: Activity, requestCode: Int) {
            startCreate(TrucoActivity::class, activity, requestCode)
        }

        fun startJoin(activity: Activity, requestCode: Int, serverDevice: BluetoothDevice) {
            startJoin(TrucoActivity::class, activity, requestCode, serverDevice)
        }
    }

}
