package ar.com.play2play.presentation.truco.lobby

import androidx.fragment.app.activityViewModels
import ar.com.play2play.presentation.lobby.ClientLobbyFragment
import ar.com.play2play.presentation.truco.TrucoViewModel

class TrucoClientLobbyFragment : ClientLobbyFragment<TrucoViewModel>() {
    override val gameViewModel: TrucoViewModel by activityViewModels()

    companion object {
        /** Creates a new instance of the [TrucoClientLobbyFragment]. */
        fun newInstance() = TrucoClientLobbyFragment()
    }
}
