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
        gameViewModel.startConnection()
        gameBinding.startButton.alpha = DISABLED_BUTTON_ALPHA
        gameBinding.startButton.setOnClickListener {
            val keyWordTheme = gameBinding.keyWordThemeField.value()
            val keyWord = gameBinding.keyWordField.value()
            viewModel.tryStartGame(keyWord, keyWordTheme)
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
        observe(viewModel.startButtonEnabled) {
            if (it) { gameBinding.startButton.alpha = ENABLED_BUTTON_ALPHA }
            else { gameBinding.startButton.alpha = DISABLED_BUTTON_ALPHA }
        }
    }

    private fun updateConnectedPlayers(players: List<String>?) {
        val text = players
            ?.takeUnless { it.isEmpty() }
            ?.joinToString()
            ?: resources.getString(R.string.lobby_no_players_yet)

        gameBinding.players.text = text
    }

    override fun onEvent(event: ImpostorCreateEvents) =
        when (event) {
            is StartGame -> gameViewModel.createGame(event.keyWord, event.keyWordTheme)
            InvalidKeyWordInput -> markErrorKeyWordInput()
            InvalidKeyWordThemeInput -> markErrorKeyWordThemeInput()
            NotEnoughPlayers -> markErrorConnectedPlayers()
        }

    private fun markErrorKeyWordInput() {
        gameBinding.keyWordField.error = resources.getString(R.string.im_validation_error_key_word_input)
        gameBinding.keyWordField.requestFocus()
    }

    private fun markErrorKeyWordThemeInput() {
        gameBinding.keyWordThemeField.error = resources.getString(R.string.im_validation_error_key_word_theme_input)
        gameBinding.keyWordThemeField.requestFocus()
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

        private const val DISABLED_BUTTON_ALPHA = 0.3F
        private const val ENABLED_BUTTON_ALPHA = 1.0F
    }
}
