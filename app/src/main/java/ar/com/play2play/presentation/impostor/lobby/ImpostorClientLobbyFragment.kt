package ar.com.play2play.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import ar.com.play2play.presentation.impostor.ImpostorViewModel
import ar.com.play2play.presentation.lobby.ClientLobbyFragment

class ImpostorClientLobbyFragment: ClientLobbyFragment<ImpostorViewModel>() {
    override val gameViewModel: ImpostorViewModel by activityViewModels()

    companion object{
        /** Create a new instance of the [ImpostorClientLobbyFragment]. */
        fun newInstance() = ImpostorClientLobbyFragment()
    }
}