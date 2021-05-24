package com.p2p.presentation.tuttifrutti.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.p2p.data.tuttifrutti.TuttiFruttiData
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.databinding.FragmentPlayTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.home.games.Game

class PlayTuttiFruttiFragment :
    BaseGameFragment<FragmentPlayTuttiFruttiBinding, TuttiFruttiPlayingEvents, PlayTuttiFruttiViewModel>() {

    override val viewModel: PlayTuttiFruttiViewModel by viewModels {
        PlayTuttiFruttiViewModelFactory(
            requireContext()
        )
    }
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTuttiFruttiBinding =
        FragmentCreateTuttiFruttiBinding::inflate

    override val gameData = Game.TUTTI_FRUTTI

    private lateinit var tuttiFruttiCategoriesAadapter: TuttiFruttiWriteCategoriesAdapter

    override fun initUI() {
        super.initUI()
        setupCategoriesRecycler()
        gameBinding.continueButton.setOnClickListener { viewModel.continueToNextScreen() }
    }

    override fun setupObservers() = with(viewModel) {
        allCategories.observe(viewLifecycleOwner) { tuttiFruttiCategoriesAadapter.categories = it }
        selectedCategories.observe(viewLifecycleOwner) {
            tuttiFruttiCategoriesAadapter.selectedCategories = it
        }
        continueButtonEnabled.observe(viewLifecycleOwner) {
            gameBinding.continueButton.isEnabled = it
        }
    }


    open override fun onEvent(event: TuttiFruttiPlayingEvents) = when (event) {
        //TODO end game for all
        EndRound -> {
        }
    }

    private fun setupCategoriesRecycler() = with(gameBinding.categoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiWriteCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@PlayTuttiFruttiFragment.tuttiFruttiCategoriesAadapter = it
        }
    }


    companion object {

        /** Create a new instance of the [PlayTuttiFruttiFragment]. */
        fun newInstance(instructions: String, tuttiFruttiData: TuttiFruttiData) =
            PlayTuttiFruttiFragment(instructions, tuttiFruttiData)
    }
}