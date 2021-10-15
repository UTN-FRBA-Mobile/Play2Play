package com.p2p.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.presentation.lobby.DefaultServerLobbyViewModel
import com.p2p.presentation.lobby.ServerLobbyFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiServerLobbyFragment : ServerLobbyFragment<TuttiFruttiViewModel>() {
    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: DefaultServerLobbyViewModel by viewModels()

    override val continueText: String by lazy { resources.getString(R.string.lobby_start_game) }
    override val continueAction = { gameViewModel.startGame() }
    override val onConnectedPlayers = { players: List<String> ->
        viewModel.updatePlayers(players)
    }


    companion object {
        /** Create a new instance of the [TuttiFruttiServerLobbyFragment]. */
        fun newInstance() = TuttiFruttiServerLobbyFragment()
    }
}