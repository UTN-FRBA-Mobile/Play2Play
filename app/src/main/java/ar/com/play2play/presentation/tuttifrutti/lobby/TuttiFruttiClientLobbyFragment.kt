package ar.com.play2play.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import ar.com.play2play.presentation.lobby.ClientLobbyFragment
import ar.com.play2play.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiClientLobbyFragment : ClientLobbyFragment<TuttiFruttiViewModel>() {
    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    companion object {
        /** Create a new instance of the [TuttiFruttiClientLobbyFragment]. */
        fun newInstance() = TuttiFruttiClientLobbyFragment()
    }


}