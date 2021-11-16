package ar.com.play2play.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.R
import ar.com.play2play.presentation.lobby.DefaultServerLobbyViewModel
import ar.com.play2play.presentation.lobby.ServerLobbyFragment
import ar.com.play2play.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiServerLobbyFragment : ServerLobbyFragment<TuttiFruttiViewModel>() {
    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: DefaultServerLobbyViewModel by viewModels()

    override val continueText: String by lazy { resources.getString(R.string.lobby_start_game) }
    override fun continueAction() { gameViewModel.startGame() }
    override fun onConnectedPlayers(players: List<String>){
        viewModel.updatePlayers(players)
    }


    companion object {
        /** Create a new instance of the [TuttiFruttiServerLobbyFragment]. */
        fun newInstance() = TuttiFruttiServerLobbyFragment()
    }
}