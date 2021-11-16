package ar.com.play2play.presentation.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ar.com.play2play.databinding.FragmentClientLobbyBinding
import ar.com.play2play.presentation.basegame.BaseGameFragment
import ar.com.play2play.presentation.basegame.GameViewModel

abstract class ClientLobbyFragment<GVM : GameViewModel> : BaseGameFragment<
        FragmentClientLobbyBinding,
        LobbyEvent,
        ClientLobbyViewModel,
        GVM>() {

    override val viewModel: ClientLobbyViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentClientLobbyBinding =
        FragmentClientLobbyBinding::inflate

    private lateinit var connectedPlayersAdapter: ConnectedPlayersAdapter

    override fun initUI() {
        super.initUI()
        gameViewModel.startConnection()
        setupPlayersRecycler()
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(players) { connectedPlayersAdapter.players = it }
        }
    }

    private fun setupPlayersRecycler() = with(gameBinding.playersRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ConnectedPlayersAdapter().also {
            this@ClientLobbyFragment.connectedPlayersAdapter = it
        }
    }

}