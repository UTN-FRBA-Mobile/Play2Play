package com.p2p.presentation.truco.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.databinding.FragmentCreateImpostorBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.impostor.ImpostorViewModel
import com.p2p.presentation.truco.TrucoViewModel
import com.p2p.presentation.truco.create.TrucoCreateEvents
import com.p2p.utils.value

class CreateTrucoFragment : BaseGameFragment<
        FragmentCreateImpostorBinding,
        TrucoCreateEvents,
        CreateTrucoViewModel,
        TrucoViewModel>() {

    override val gameViewModel: TrucoViewModel by activityViewModels()
    override val viewModel: CreateTrucoViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateImpostorBinding =
        FragmentCreateImpostorBinding::inflate

    override fun initUI() {
        super.initUI()
        gameViewModel.startConnection()
        gameBinding.startButton.setOnClickListener {
            val keyWord = gameBinding.textField.value()
            viewModel.tryStartGame(keyWord)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(players) {
                val otherPlayers = getOtherPlayers()
                updateConnectedPlayers(otherPlayers)
                viewModel.updatePlayers(otherPlayers)
            }
        }
    }

    private fun updateConnectedPlayers(players: List<String>?) {
        val text = players
            ?.takeUnless { it.isEmpty() }
            ?.joinToString()
            ?: resources.getString(R.string.lobby_no_players_yet)

        gameBinding.players.text = text
    }

    override fun onEvent(event: TrucoCreateEvents) =
        when (event) {
            is StartGame -> gameViewModel.startGame()
            NoConnectedPlayers -> markErrorConnectedPlayers()
        }


    private fun markErrorConnectedPlayers() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.im_validation_error_players))
            //It is positive to be shown on the right
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    companion object {

        /** Create a new instance of the [CreateTrucoFragment]. */
        fun newInstance() = CreateTrucoFragment()
    }
}
