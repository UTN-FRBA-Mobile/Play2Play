package com.p2p.presentation.clientlobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentClientLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.basegame.GameViewModel

class ClientLobbyFragment(override val gameViewModel: GameViewModel) : BaseGameFragment<
        FragmentClientLobbyBinding,
        LobbyEvent,
        ClientLobbyViewModel,
        GameViewModel>() {

    override val viewModel: ClientLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentClientLobbyBinding =
        FragmentClientLobbyBinding::inflate

    private lateinit var connectedPlayersAdapter: ConnectedPlayersAdapter

    override fun initUI() {
        super.initUI()
        gameViewModel.startConnection()
        setupPlayersRecycler()
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(players) { connectedPlayersAdapter.players = it }
        }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersAdapter().also {
            this@ClientLobbyFragment.connectedPlayersAdapter = it
        }
    }


    companion object {
        fun newInstance(gameViewModel: GameViewModel) = ClientLobbyFragment(gameViewModel)
    }
}