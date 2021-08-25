package com.p2p.presentation.truco.lobby

import android.content.ClipData
import android.content.ClipDescription
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemLongClickListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.databinding.FragmentTrucoServerLobbyBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.truco.TrucoViewModel
import com.p2p.utils.fromHtml


class ServerTrucoLobbyFragment : BaseGameFragment<
        FragmentTrucoServerLobbyBinding,
        LobbyEvent,
        ServerTrucoLobbyViewModel,
        TrucoViewModel>() {

    override val gameViewModel: TrucoViewModel by activityViewModels()

    override val viewModel: ServerTrucoLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTrucoServerLobbyBinding =
        FragmentTrucoServerLobbyBinding::inflate

    private lateinit var connectedPlayersTrucoAdapter: ConnectedPlayersTrucoAdapter

    // TODO: Delete mocks
    private val mockedPlayers: List<String> = listOf("Homero", "Marge", "Bart", "Lisa")

    override fun initValues() {
        observe(gameViewModel.players) { viewModel.setPlayers(mockedPlayers) }
        observe(gameViewModel.totalPlayers) { viewModel.setTotalPlayers(it) }
    }

    override fun initUI() {
        super.initUI()
        gameViewModel.startConnection()
        setupPlayersGrid()
        gameBinding.startGameButton.setOnClickListener {
            gameViewModel.startGame()
        }
        gameBinding.helpOrderPlayersDescription.text = resources
            .getString(R.string.tr_lobby_help_order_players)
            .fromHtml()
        // TODO: Add drag and drop functionality. gameBinding.playersGrid.onItemLongClickListener = OnItemLongClickListener {}
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(myDeviceName) {
                gameBinding.helpPlayersDescription.text = resources
                    .getString(R.string.lobby_give_help_players_decription, it)
                    .fromHtml()
            }
            observe(players) {
                connectedPlayersTrucoAdapter.players = mockedPlayers
            }
            observe(totalPlayers) {
                connectedPlayersTrucoAdapter.totalPlayers = it
            }
        }
        observe(viewModel.isContinueButtonEnabled) { gameBinding.startGameButton.isEnabled = it }
    }

    private fun setupPlayersGrid() = with(gameBinding.playersGrid) {
        adapter = activity?.let {
            ConnectedPlayersTrucoAdapter(it)
                .also {
                this@ServerTrucoLobbyFragment.connectedPlayersTrucoAdapter = it
            }
        }
    }

    override fun onEvent(event: LobbyEvent) {
        when (event) {
            is GoToPlay -> Unit
        }
    }

    companion object {
        fun newInstance() = ServerTrucoLobbyFragment()
    }
}
