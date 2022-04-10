package ar.com.play2play.presentation.tuttifrutti

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import ar.com.play2play.presentation.basegame.GameActivity
import ar.com.play2play.presentation.tuttifrutti.countdown.TuttiFruttiCountdownFragment
import ar.com.play2play.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment
import ar.com.play2play.presentation.tuttifrutti.finalscore.FinalScoreTuttiFruttiFragment
import ar.com.play2play.presentation.tuttifrutti.lobby.TuttiFruttiClientLobbyFragment
import ar.com.play2play.presentation.tuttifrutti.lobby.TuttiFruttiServerLobbyFragment
import ar.com.play2play.presentation.tuttifrutti.review.TuttiFruttiReviewFragment
import ar.com.play2play.presentation.tuttifrutti.review.client.TuttiFruttiClientReviewFragment

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
        addFragment(TuttiFruttiClientLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun goToServerLobby() =
        addFragment(TuttiFruttiServerLobbyFragment.newInstance(), shouldAddToBackStack = false)

    override fun onGameEvent(event: TuttiFruttiSpecificGameEvent) {
        super.onGameEvent(event)
        when (event) {
            GoToFinalScore -> addFragment(
                FinalScoreTuttiFruttiFragment.newInstance(),
                shouldAddToBackStack = false
            )
            GoToReview ->
                addFragment(TuttiFruttiReviewFragment.newInstance(), shouldAddToBackStack = false)
            GoToClientReview ->
                addFragment(TuttiFruttiClientReviewFragment.newInstance(), shouldAddToBackStack = false)
            else -> Unit
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
