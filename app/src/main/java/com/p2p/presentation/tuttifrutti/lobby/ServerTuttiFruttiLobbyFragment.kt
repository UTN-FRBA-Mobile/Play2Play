package com.p2p.presentation.tuttifrutti.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentTuttiFruttiServerLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class ServerTuttiFruttiLobbyFragment: BaseGameFragment<
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
        gameBinding.startGame.isEnabled = false
        setupPlayersRecycler()
        gameBinding.startGame.setOnClickListener {
            gameViewModel.startGame()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            players.observe(viewLifecycleOwner) {
                connectedPlayersTuttiFruttiAdapter.players = it
                if (it.size >= LOBBY_MIN_SIZE) gameBinding.startGame.isEnabled = true
            }
        }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersTuttiFruttiAdapter().also {
            this@ServerTuttiFruttiLobbyFragment.connectedPlayersTuttiFruttiAdapter = it
        }
    }

    companion object {
        fun newInstance() = ServerTuttiFruttiLobbyFragment()

        const val LOBBY_MIN_SIZE = 1
    }
}