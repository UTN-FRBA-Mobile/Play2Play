package com.p2p.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import com.p2p.presentation.impostor.ImpostorViewModel
import com.p2p.presentation.lobby.ClientLobbyFragment

class ImpostorClientLobbyFragment: ClientLobbyFragment<ImpostorViewModel>() {
    override val gameViewModel: ImpostorViewModel by activityViewModels()

    companion object{
        /** Create a new instance of the [ImpostorClientLobbyFragment]. */
        fun newInstance() = ImpostorClientLobbyFragment()
    }
}