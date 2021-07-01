package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.p2p.R
import com.p2p.model.VisibleLoadingScreen
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.tuttifrutti.lobby.ClientTuttiFruttiLobbyFragment
import com.p2p.presentation.tuttifrutti.lobby.ServerTuttiFruttiLobbyFragment
import com.p2p.presentation.tuttifrutti.countdown.TuttiFruttiCountdownFragment
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment
import com.p2p.utils.hideKeyboard

class TuttiFruttiActivity : GameActivity<TuttiFruttiSpecificGameEvent, TuttiFruttiViewModel>(
    R.layout.activity_tutti_frutti
) {

    override val viewModel: TuttiFruttiViewModel by viewModels {
        TuttiFruttiViewModelFactory(this, gameViewModelFactoryData)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadingScreen.observe(this) { loading ->
            if (loading.isLoading) hideKeyboard()
            findViewById<View>(R.id.activity_progress_overlay).isVisible = loading.isLoading
            when (loading) {
                is VisibleLoadingScreen ->
                    findViewById<TextView>(R.id.progress_text).text = loading.waitingText
                else -> {
                }
            }
        }
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

    companion object {

        fun startCreate(context: Context) = startCreate(TuttiFruttiActivity::class, context)

        fun startJoin(context: Context, serverDevice: BluetoothDevice) {
            startJoin(TuttiFruttiActivity::class, context, serverDevice)
        }
    }
}
