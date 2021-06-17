package com.p2p.presentation.tuttifrutti.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentTuttiFruttiClientLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class ClientTuttiFruttiLobbyFragment: BaseGameFragment<
        FragmentTuttiFruttiClientLobbyBinding,
        LobbyEvent,
        ClientTuttiFruttiLobbyViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: ClientTuttiFruttiLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTuttiFruttiClientLobbyBinding =
        FragmentTuttiFruttiClientLobbyBinding::inflate

    private lateinit var connectedPlayersTuttiFruttiAdapter: ConnectedPlayersTuttiFruttiAdapter

    override fun initUI() {
        super.initUI()
        setupPlayersRecycler()
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            players.observe(viewLifecycleOwner) { connectedPlayersTuttiFruttiAdapter.players = it }
        }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersTuttiFruttiAdapter().also {
            this@ClientTuttiFruttiLobbyFragment.connectedPlayersTuttiFruttiAdapter = it
        }
    }

    companion object {
        fun newInstance() = ClientTuttiFruttiLobbyFragment()
    }
}