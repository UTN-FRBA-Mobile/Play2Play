package com.p2p.presentation.impostor.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.databinding.FragmentCreateImpostorBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.impostor.ImpostorViewModel
import com.p2p.utils.fromHtml
import com.p2p.utils.value

class CreateImpostorFragment : BaseGameFragment<
        FragmentCreateImpostorBinding,
        ImpostorCreateEvents,
        CreateImpostorViewModel,
        ImpostorViewModel>() {

    override val gameViewModel: ImpostorViewModel by activityViewModels()
    override val viewModel: CreateImpostorViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateImpostorBinding =
        FragmentCreateImpostorBinding::inflate

    override fun initUI() {
        super.initUI()
        gameBinding.startButton.setOnClickListener {
            val keyWord = gameBinding.textField.value()
            viewModel.tryStartGame(keyWord)
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(players) {
                val otherPlayers = otherPlayers()
                updateConnectedPlayers(otherPlayers)
                viewModel.updatePlayers(otherPlayers)
            }
        }
    }

    private fun updateConnectedPlayers(players: List<String>?) {
        val text = if (players?.isNotEmpty() == true)
            players.joinToString(", ")
        else resources.getString(R.string.lobby_no_players_yet)

        gameBinding.players.text = text
    }

    override fun onEvent(event: ImpostorCreateEvents) =
        when (event) {
            is StartGame -> gameViewModel.startGame(event.keyWord)
            InvalidInput -> markErrorInput()
            NoConnectedPlayers -> markErrorConnectedPlayers()
        }

    private fun markErrorInput() {
        gameBinding.textField.error = resources.getString(R.string.im_validation_error_input)
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

        /** Create a new instance of the [CreateImpostorFragment]. */
        fun newInstance() = CreateImpostorFragment()
    }
}