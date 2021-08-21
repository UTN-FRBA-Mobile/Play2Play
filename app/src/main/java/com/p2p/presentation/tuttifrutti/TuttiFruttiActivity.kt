package com.p2p.presentation.tuttifrutti

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.truco.lobby.ServerTrucoLobbyFragment
import com.p2p.presentation.tuttifrutti.countdown.TuttiFruttiCountdownFragment
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment
import com.p2p.presentation.tuttifrutti.finalscore.FinalScoreTuttiFruttiFragment
import com.p2p.presentation.tuttifrutti.lobby.ClientTuttiFruttiLobbyFragment
import com.p2p.presentation.tuttifrutti.lobby.ServerTuttiFruttiLobbyFragment
import com.p2p.presentation.tuttifrutti.review.TuttiFruttiReviewFragment

class TuttiFruttiActivity : GameActivity<TuttiFruttiSpecificGameEvent, TuttiFruttiViewModel>() {

    override val viewModel: TuttiFruttiViewModel by viewModels {
        TuttiFruttiViewModelFactory(this, gameViewModelFactoryData)
    }

    override fun goToCreate() =
        addFragment(CreateTuttiFruttiFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToPlay() {
        viewModel.stopLoading()
        addFragment(TuttiFruttiCountdownFragment.newInstance(), shouldAddToBackStack = false)
    }

    override fun goToClientLobby() =
        addFragment(ClientTuttiFruttiLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToServerLobby() =
        addFragment(ServerTuttiFruttiLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun onGameEvent(event: TuttiFruttiSpecificGameEvent) {
        super.onGameEvent(event)
        when (event) {
            GoToFinalScore -> addFragment(
                FinalScoreTuttiFruttiFragment.newInstance(),
                shouldAddToBackStack = false
            )
            GoToReview -> addFragment(
                TuttiFruttiReviewFragment.newInstance(),
                shouldAddToBackStack = false
            )
        }
    }

    companion object {

        fun startCreate(activity: Activity, requestCode: Int) {
            startCreate(TuttiFruttiActivity::class, activity, requestCode)
        }

        fun startJoin(activity: Activity, requestCode: Int, serverDevice: BluetoothDevice) {
            startJoin(TuttiFruttiActivity::class, activity, requestCode, serverDevice)
        }
    }
}
