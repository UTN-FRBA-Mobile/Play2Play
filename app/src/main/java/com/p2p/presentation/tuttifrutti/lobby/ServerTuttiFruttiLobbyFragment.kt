package com.p2p.presentation.tuttifrutti.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentTuttiFruttiServerLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class ServerTuttiFruttiLobbyFragment : BaseGameFragment<
        FragmentTuttiFruttiServerLobbyBinding,
        LobbyEvent,
        ServerTuttiFruttiLobbyViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: ServerTuttiFruttiLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTuttiFruttiServerLobbyBinding =
        FragmentTuttiFruttiServerLobbyBinding::inflate

    private lateinit var connectedPlayersTuttiFruttiAdapter: ConnectedPlayersTuttiFruttiAdapter

    override fun initUI() {
        super.initUI()
        gameViewModel.startConnection()
        setupPlayersRecycler()
        gameBinding.startGameButton.setOnClickListener {
            gameViewModel.startGame()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(players) {
                connectedPlayersTuttiFruttiAdapter.players = it
                viewModel.updatePlayers(it)
            }
        }
        observe(viewModel.isContinueButtonEnabled) { gameBinding.startGameButton.isEnabled = it }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersTuttiFruttiAdapter().also {
            this@ServerTuttiFruttiLobbyFragment.connectedPlayersTuttiFruttiAdapter = it
        }
    }

    override fun onEvent(event: LobbyEvent) {
        when (event) {
            is GoToPlay -> Unit
        }
    }

    companion object {
        fun newInstance() = ServerTuttiFruttiLobbyFragment()
    }
}
