package ar.com.play2play.presentation.truco.lobby

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.R
import ar.com.play2play.presentation.lobby.ServerLobbyFragment
import ar.com.play2play.presentation.truco.TrucoViewModel
import ar.com.play2play.presentation.truco.lobby.server.TrucoServerLobbyViewModel

class TrucoServerLobbyFragment : ServerLobbyFragment<TrucoViewModel>() {
    override val gameViewModel: TrucoViewModel by activityViewModels()

    override val viewModel: TrucoServerLobbyViewModel by viewModels()
    var totalPlayers: Int? = null

    override val continueText: String by lazy { resources.getString(R.string.lobby_build_teams) }

    override fun onConnectedPlayers(players: List<String>){
        totalPlayers?.run { viewModel.updatePlayers(players, this) }
    }

    override fun continueAction(){
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