package com.p2p.presentation.tuttifrutti.lobby

import androidx.fragment.app.activityViewModels
import com.p2p.presentation.lobby.ServerLobbyFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiServerLobbyFragment : ServerLobbyFragment<TuttiFruttiViewModel>() {
    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()


    companion object {
        /** Create a new instance of the [TuttiFruttiServerLobbyFragment]. */
        fun newInstance() = TuttiFruttiServerLobbyFragment()
    }
}