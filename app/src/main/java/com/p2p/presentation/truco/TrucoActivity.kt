package com.p2p.presentation.truco

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.truco.create.CreateTrucoFragment
import com.p2p.presentation.truco.finalscore.FinalScoreTrucoFragment
import com.p2p.presentation.truco.lobby.TrucoBuildTeamsFragment
import com.p2p.presentation.truco.lobby.TrucoClientLobbyFragment
import com.p2p.presentation.truco.lobby.TrucoServerLobbyFragment

class TrucoActivity : GameActivity<TrucoSpecificGameEvent, TrucoViewModel>() {

    override val viewModel: TrucoViewModel by viewModels {
        TrucoViewModelFactory(this, gameViewModelFactoryData)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun goToCreate() = addFragment(CreateTrucoFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToClientLobby() =
        addFragment(TrucoClientLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToServerLobby() =
        addFragment(TrucoServerLobbyFragment.newInstance(), shouldAddToBackStack = false)

    fun goToBuildTeams() =
        addFragment(TrucoBuildTeamsFragment.newInstance(), shouldAddToBackStack = false)

    override fun onGameEvent(event: TrucoSpecificGameEvent) {
        when (event) {
            is TrucoFinishGame -> {
                // We delete the bottom sheet fragment from the truco game
                (supportFragmentManager.findFragmentByTag(TrucoFragment.ACTIONS_BOTTOM_SHEET_TAG) as BottomSheetDialogFragment?)?.dismiss()
                addFragment(FinalScoreTrucoFragment.newInstance(), shouldAddToBackStack = false)
            }
            is TrucoGoToPlay -> goToPlay(event.playersQuantity)
            is TrucoGoToBuildTeams -> goToBuildTeams()
            else -> super.onGameEvent(event)
        }
    }

    private fun goToPlay(playersQuantity: Int) {
        viewModel.stopLoading()
        when (playersQuantity) {
            2 -> addFragment(TrucoPlayFor2Fragment.newInstance(), shouldAddToBackStack = false)
            4 -> addFragment(TrucoPlayFor4Fragment.newInstance(), shouldAddToBackStack = false)
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

    // unused.
    override fun goToPlay() {
        Unit
    }

}
