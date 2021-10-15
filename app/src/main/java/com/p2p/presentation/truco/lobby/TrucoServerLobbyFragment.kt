package com.p2p.presentation.truco.lobby

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.presentation.lobby.ServerLobbyFragment
import com.p2p.presentation.truco.TrucoViewModel
import com.p2p.presentation.truco.lobby.server.TrucoServerLobbyViewModel

class TrucoServerLobbyFragment : ServerLobbyFragment<TrucoViewModel>() {
    override val gameViewModel: TrucoViewModel by activityViewModels()

    override val viewModel: TrucoServerLobbyViewModel by viewModels()
    var totalPlayers: Int? = null

    override val continueText: String by lazy { resources.getString(R.string.lobby_build_teams) }

    override val onConnectedPlayers: (List<String>) -> Unit = { players ->
        totalPlayers?.run { viewModel.updatePlayers(players, this) }
    }

    override val continueAction = {
        gameViewModel.goToBuildTeams()
    }

    override fun setupObservers() {
        observe(gameViewModel.totalPlayers) {
            totalPlayers = it
        }
        super.setupObservers()
    }

    companion object {
        /** Create a new instance of the [TrucoServerLobbyFragment]. */
        fun newInstance() = TrucoServerLobbyFragment()
    }
}