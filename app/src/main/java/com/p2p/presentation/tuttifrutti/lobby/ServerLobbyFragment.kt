package com.p2p.presentation.tuttifrutti.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentServerLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.create.rounds.RoundsNumberFragment

class ServerLobbyFragment: BaseGameFragment<
        FragmentServerLobbyBinding,
        LobbyEvent,
        ServerLobbyViewModel,
        LobbyGameViewModel>() {
    override val gameViewModel: LobbyGameViewModel by activityViewModels()

    override val viewModel: ServerLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentServerLobbyBinding =
        FragmentServerLobbyBinding::inflate

    private lateinit var connectedPlayersAdapter: ConnectedPlayersAdapter

    override fun initUI() {
        super.initUI()
        gameBinding.startGame.isEnabled = false
        setupPlayersRecycler()
        gameBinding.startGame.setOnClickListener {
            gameViewModel.closeDiscovery()
            gameViewModel.goToPlay()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            players.observe(viewLifecycleOwner) {
                connectedPlayersAdapter.players = it
                if (it.size >= LOBBY_MIN_SIZE) gameBinding.startGame.isEnabled = true
            }
        }
        with(viewModel) {
            goToPlayButtonEnabled.observe(viewLifecycleOwner) {
                gameBinding.startGame.isEnabled = it
            }
        }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersAdapter().also {
            this@ServerLobbyFragment.connectedPlayersAdapter = it
        }
    }

    override fun onEvent(event: LobbyEvent) = when (event) {
        is GoToPlay -> {
            RoundsNumberFragment.newInstance().show(childFragmentManager, "RoundsNumberDialog")
        }
        else -> Unit
    }

    companion object {
        fun newInstance() = ServerLobbyFragment()

        const val LOBBY_MIN_SIZE = 2
    }
}