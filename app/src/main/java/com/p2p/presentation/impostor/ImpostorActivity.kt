package com.p2p.presentation.impostor

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.impostor.create.CreateImpostorFragment
import com.p2p.presentation.impostor.play.PlayImpostorFragment
import com.p2p.presentation.impostor.play.PlayInfoImpostorFragment
import com.p2p.presentation.tuttifrutti.lobby.ImpostorClientLobbyFragment

class ImpostorActivity : GameActivity<ImpostorSpecificGameEvent, ImpostorViewModel>() {

    override val viewModel: ImpostorViewModel by viewModels {
        ImpostorViewModelFactory(this, gameViewModelFactoryData)
    }

    override fun goToCreate() =
        addFragment(CreateImpostorFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToPlay() {
        val fragmentToShow =
            if (viewModel.isServer()) PlayInfoImpostorFragment.newInstance() else PlayImpostorFragment.newInstance()
        addFragment(fragmentToShow, shouldAddToBackStack = false)
    }

    override fun goToClientLobby() =
        addFragment(ImpostorClientLobbyFragment.newInstance(), shouldAddToBackStack = false)

    companion object {

        fun startCreate(activity: Activity, requestCode: Int) {
            startCreate(ImpostorActivity::class, activity, requestCode)
        }

        fun startJoin(activity: Activity, requestCode: Int, serverDevice: BluetoothDevice) {
            startJoin(ImpostorActivity::class, activity, requestCode, serverDevice)
        }
    }
}
