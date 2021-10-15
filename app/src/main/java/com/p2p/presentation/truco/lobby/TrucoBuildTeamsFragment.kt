package com.p2p.presentation.truco.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.databinding.FragmentTrucoServerBuildTeamsBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.truco.TrucoViewModel
import com.p2p.utils.fromHtml

class TrucoBuildTeamsFragment : BaseGameFragment<
        FragmentTrucoServerBuildTeamsBinding,
        LobbyEvent,
        ServerTrucoLobbyViewModel,
        TrucoViewModel>() {

    override val gameViewModel: TrucoViewModel by activityViewModels()

    override val viewModel: ServerTrucoLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTrucoServerBuildTeamsBinding =
        FragmentTrucoServerBuildTeamsBinding::inflate

    private lateinit var connectedPlayersTrucoAdapter: ConnectedPlayersTrucoAdapter

    override fun initValues() {
        observe(gameViewModel.players) { viewModel.setPlayers(it) }
        observe(gameViewModel.totalPlayers) { viewModel.setTotalPlayers(it) }
    }

    override fun initUI() {
        super.initUI()
        setupPlayersGrid()
        gameBinding.helpOrderPlayersDescription.text = resources
            .getString(R.string.tr_lobby_help_order_players)
            .fromHtml()
        gameBinding.startGameButton.setOnClickListener { gameViewModel.startGame() }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(players) {
                connectedPlayersTrucoAdapter.players = it
                viewModel.setPlayers(it)
            }
            observe(totalPlayers) {
                connectedPlayersTrucoAdapter.totalPlayers = it
                viewModel.setTotalPlayers(it)
            }
        }
        observe(viewModel.isContinueButtonEnabled) { gameBinding.startGameButton.isEnabled = it }
    }

    private fun setupPlayersGrid() = with(gameBinding.playersGrid) {
        adapter = activity?.let {
            ConnectedPlayersTrucoAdapter(it)
                .also { adapter ->
                    this@TrucoBuildTeamsFragment.connectedPlayersTrucoAdapter = adapter
                }
        }
    }

    override fun onEvent(event: LobbyEvent) {
        when (event) {
            is GoToPlay -> Unit
        }
    }

    companion object {
        fun newInstance() = TrucoBuildTeamsFragment()
    }
}
