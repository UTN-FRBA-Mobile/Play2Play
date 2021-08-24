package com.p2p.presentation.truco.lobby

import androidx.fragment.app.activityViewModels
import com.p2p.presentation.lobby.ClientLobbyFragment
import com.p2p.presentation.truco.TrucoViewModel
import com.p2p.utils.Logger

class TrucoClientLobbyFragment : ClientLobbyFragment<TrucoViewModel>() {
    override val gameViewModel: TrucoViewModel by activityViewModels()

    init {
        Logger.d("P2P_TrucoClientLobbyFragment", "STARTING FRAGMENT")
    }

    companion object {
        /** Creates a new instance of the [TrucoClientLobbyFragment]. */
        fun newInstance() = TrucoClientLobbyFragment()
    }
}
