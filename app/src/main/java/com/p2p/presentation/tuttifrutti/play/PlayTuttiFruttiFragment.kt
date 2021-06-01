package com.p2p.presentation.tuttifrutti.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.model.tuttifrutti.TuttiFruttiInfo
import com.p2p.databinding.FragmentPlayTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.categories.TuttiFruttiCategoriesEvents

class PlayTuttiFruttiFragment :BaseGameFragment<
        FragmentPlayTuttiFruttiBinding,
        TuttiFruttiPlayingEvents,
        PlayTuttiFruttiViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: PlayTuttiFruttiViewModel by viewModels()

    val gameInfo: TuttiFruttiInfo by lazy {
        requireNotNull(requireArguments().getParcelable(GAME_INFO_KEY))
        { "Game info must be passed to fragment arguments" }
    }

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTuttiFruttiBinding =
        FragmentPlayTuttiFruttiBinding::inflate


    private lateinit var tuttiFruttiCategoriesAdapter: TuttiFruttiWriteCategoriesAdapter

    override fun initUI() {
        super.initUI()
        setupCategoriesRecycler()
        with(gameBinding){
            //Todo set random letter and check on rounds letters that that letter has not been used
            roundLetter.text = "A"
            roundNumber.text = "1/" + gameInfo.totalRounds ?: ""
            finishRoundButton.setOnClickListener { viewModel.finishRound(gameInfo.categories) }
        }
    }


    override fun onEvent(event: TuttiFruttiPlayingEvents) = when (event) {
        //TODO end round and pass to next stage
        EndRound -> { }
        InvalidInputs -> showErrorMessage()
    }


    //TODO replace by material validations
    private fun showErrorMessage() {MaterialAlertDialogBuilder(requireContext())
        .setMessage("Error de las instrucciones")
        //It is positive to be shown on the right
        .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
            // Respond to positive button press
        }
        .show()
    }

    private fun setupCategoriesRecycler() = with(gameBinding.categoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter =
            TuttiFruttiWriteCategoriesAdapter(gameInfo.categories, viewModel::onFocusOut).also {
                this@PlayTuttiFruttiFragment.tuttiFruttiCategoriesAdapter = it
            }
    }


    companion object {

        const val GAME_INFO_KEY = "GameInfo"

        /** Create a new instance of the [PlayTuttiFruttiFragment]. */
        fun newInstance(gameInfo: TuttiFruttiInfo) =
            PlayTuttiFruttiFragment().apply {
                arguments = bundleOf(
                    GAME_INFO_KEY to gameInfo
                )
            }
    }
}