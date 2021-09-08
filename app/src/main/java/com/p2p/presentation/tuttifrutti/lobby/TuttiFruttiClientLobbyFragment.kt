package com.p2p.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import com.p2p.presentation.lobby.ClientLobbyFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiClientLobbyFragment : ClientLobbyFragment<TuttiFruttiViewModel>() {
    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    companion object {
        /** Create a new instance of the [TuttiFruttiClientLobbyFragment]. */
        fun newInstance() = TuttiFruttiClientLobbyFragment()
    }


}