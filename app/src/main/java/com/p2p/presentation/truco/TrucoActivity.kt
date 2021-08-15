package com.p2p.presentation.truco

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import com.p2p.presentation.basegame.GameActivity

class TrucoActivity : GameActivity<TrucoSpecificGameEvent, TrucoViewModel>() {

    override val viewModel: TrucoViewModel by viewModels {
        TrucoViewModelFactory(this, gameViewModelFactoryData)
    }

    // TODO: Create truco game
    override fun goToCreate() = TODO("Not yet implemented")

    override fun goToPlay() {
        viewModel.stopLoading()
        // TODO: Start truco game
    }

    // TODO: Implement truco lobbys
   override fun goToClientLobby() = TODO("Not yet implemented")

    override fun goToServerLobby() = TODO("Not yet implemented")


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