package com.p2p.presentation.tuttifrutti.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentClientLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.ClientTuttiFruttiViewModel

class ClientLobbyFragment: BaseGameFragment<
        FragmentClientLobbyBinding,
        LobbyEvent,
        ClientLobbyViewModel,
        ClientTuttiFruttiViewModel>() {

    override val gameViewModel: ClientTuttiFruttiViewModel by activityViewModels()

    override val viewModel: ClientLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentClientLobbyBinding =
        FragmentClientLobbyBinding::inflate

    private lateinit var connectedPlayersAdapter: ConnectedPlayersAdapter

    override fun initUI() {
        super.initUI()
        setupPlayersRecycler()
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            players.observe(viewLifecycleOwner) { connectedPlayersAdapter.players = it }
        }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersAdapter().also {
            this@ClientLobbyFragment.connectedPlayersAdapter = it
        }
    }

    companion object {
        fun newInstance() = ClientLobbyFragment()
    }
}