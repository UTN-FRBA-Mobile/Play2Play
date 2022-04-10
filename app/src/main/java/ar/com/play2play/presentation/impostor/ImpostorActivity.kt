package ar.com.play2play.presentation.impostor

import android.app.Activity
import android.bluetooth.BluetoothDevice
import androidx.activity.viewModels
import ar.com.play2play.presentation.basegame.GameActivity
import ar.com.play2play.presentation.impostor.create.CreateImpostorFragment
import ar.com.play2play.presentation.impostor.play.PlayImpostorFragment
import ar.com.play2play.presentation.impostor.play.PlayInfoImpostorFragment
import ar.com.play2play.presentation.tuttifrutti.lobby.ImpostorClientLobbyFragment

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
