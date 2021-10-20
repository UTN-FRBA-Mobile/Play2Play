package com.p2p.presentation.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.R
import com.p2p.databinding.FragmentServerLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.utils.fromHtml

abstract class ServerLobbyFragment<GVM : GameViewModel> : BaseGameFragment<
        FragmentServerLobbyBinding,
        LobbyEvent,
        ServerLobbyViewModel,
        GVM>() {

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentServerLobbyBinding =
        FragmentServerLobbyBinding::inflate

    private lateinit var connectedPlayersAdapter: ConnectedPlayersAdapter

    protected abstract val continueText: String
    protected abstract fun continueAction()
    protected open fun onConnectedPlayers(players: List<String>) {}

    override fun initUI() {
        super.initUI()
        gameViewModel.startConnection()
        setupPlayersRecycler()
        gameBinding.nextScreenButton.setOnClickListener {
            continueAction()
        }
        gameBinding.nextScreenButton.text = continueText
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(myDeviceName) {
                gameBinding.helpPlayersDescription.text = resources
                    .getString(R.string.lobby_give_help_players_decription, it)
                    .fromHtml()
            }
            observe(players) {
                connectedPlayersAdapter.players = it
                onConnectedPlayers(it)
            }
        }
        observe(viewModel.isContinueButtonEnabled) { gameBinding.nextScreenButton.isEnabled = it }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersAdapter().also {
            this@ServerLobbyFragment.connectedPlayersAdapter = it
        }
    }

    override fun onEvent(event: LobbyEvent) {
        when (event) {
            is GoToPlay -> Unit
        }
    }
}
