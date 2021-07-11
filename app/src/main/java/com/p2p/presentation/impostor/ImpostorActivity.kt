package com.p2p.presentation.impostor

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.impostor.create.CreateImpostorFragment
import com.p2p.presentation.tuttifrutti.countdown.TuttiFruttiCountdownFragment
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment
import com.p2p.presentation.tuttifrutti.finalscore.FinalScoreTuttiFruttiFragment
import com.p2p.presentation.tuttifrutti.lobby.ClientTuttiFruttiLobbyFragment
import com.p2p.presentation.tuttifrutti.lobby.ServerTuttiFruttiLobbyFragment
import com.p2p.presentation.tuttifrutti.review.TuttiFruttiReviewFragment

class ImpostorActivity : GameActivity<ImpostorSpecificGameEvent, ImpostorViewModel>() {

    override val viewModel: ImpostorViewModel by viewModels {
        ImpostorViewModelFactory(this, gameViewModelFactoryData)
    }

    override fun goToCreate() =
        addFragment(CreateImpostorFragment.newInstance(), shouldAddToBackStack = false)

    //TODO bren change
    override fun goToPlay() {
        viewModel.stopLoading()
        addFragment(TuttiFruttiCountdownFragment.newInstance(), shouldAddToBackStack = false)
    }

    //TODO bren change
    override fun goToClientLobby() =
        addFragment(ClientTuttiFruttiLobbyFragment.newInstance(), shouldAddToBackStack = false)

    //TODO bren change
    override fun goToServerLobby() =
        addFragment(ServerTuttiFruttiLobbyFragment.newInstance(), shouldAddToBackStack = false)

    //TODO bren change
    override fun onGameEvent(event: ImpostorSpecificGameEvent) {
        super.onGameEvent(event)
        when (event) {
            //TODO bren change
            StartGame -> addFragment(
                FinalScoreTuttiFruttiFragment.newInstance(),
                shouldAddToBackStack = false
            )
        }
    }

    companion object {

        fun startCreate(activity: Activity, requestCode: Int) {
            startCreate(ImpostorActivity::class, activity, requestCode)
        }

        fun startJoin(activity: Activity, requestCode: Int, serverDevice: BluetoothDevice) {
            startJoin(ImpostorActivity::class, activity, requestCode, serverDevice)
        }
    }
}
