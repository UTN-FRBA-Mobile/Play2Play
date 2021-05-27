package com.p2p.presentation.tuttifrutti.create.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.model.tuttifrutti.TuttiFruttiInfo
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.play.PlayTuttiFruttiFragment
import com.p2p.presentation.tuttifrutti.create.rounds.RoundsNumberFragment

class CreateTuttiFruttiFragment :
        BaseGameFragment<FragmentCreateTuttiFruttiBinding, TuttiFruttiCategoriesEvents, CreateTuttiFruttiViewModel>() {

    override val viewModel: CreateTuttiFruttiViewModel by viewModels {
        CreateTuttiFruttiViewModelFactory(
                requireContext()
        )
    }
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTuttiFruttiBinding =
            FragmentCreateTuttiFruttiBinding::inflate

    override val gameData = Game.TUTTI_FRUTTI

    override val instructions: String by lazy {
        requireNotNull(requireArguments().getString(INSTRUCTIONS_KEY))
        { "Instructions key must be passed to fragment arguments" }
    }

    private lateinit var tuttiFruttiCategoriesAdapter: TuttiFruttiCategoriesAdapter
    private lateinit var tuttiFruttiSelectedCategoriesAdapter: TuttiFruttiSelectedCategoriesAdapter

    override fun initUI() {
        super.initUI()
        setupCategoriesRecycler()
        setupCategoriesSelectedRecycler()
        gameBinding.continueButton.setOnClickListener { viewModel.continueToNextScreen() }
    }

    override fun setupObservers() = with(viewModel) {
        allCategories.observe(viewLifecycleOwner) { tuttiFruttiCategoriesAdapter.categories = it }
        selectedCategories.observe(viewLifecycleOwner) {
            tuttiFruttiCategoriesAdapter.selectedCategories = it
            tuttiFruttiSelectedCategoriesAdapter.selectedCategories = it
        }
        continueButtonEnabled.observe(viewLifecycleOwner) {
            gameBinding.continueButton.isEnabled = it
        }
    }


    override fun onEvent(event: TuttiFruttiCategoriesEvents) = when (event) {
        GoToSelectRounds -> RoundsNumberFragment.newInstance().show(childFragmentManager, "RoundsNumberDialog")
        //TODO call next step of creating game instead of game fragment
        ContinueCreatingGame -> {
            val selectedCategories = ArrayList(viewModel.selectedCategories.value)
            addFragment(
                PlayTuttiFruttiFragment.newInstance(
                    instructions,
                    TuttiFruttiInfo(totalRounds = 10, selectedCategories)
                ),
                shouldAddToBackStack = true
            )

        }
    }

    private fun setupCategoriesRecycler() = with(gameBinding.categoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiCategoriesAdapter = it
        }
    }

    private fun setupCategoriesSelectedRecycler() = with(gameBinding.categoriesSelectedRecycler) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = TuttiFruttiSelectedCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiSelectedCategoriesAdapter = it
        }
    }

    companion object {

        const val INSTRUCTIONS_KEY = "Instructions"

        /** Create a new instance of the [CreateTuttiFruttiFragment]. */
        fun newInstance(instructions: String) =
            CreateTuttiFruttiFragment().apply {
                arguments = bundleOf(INSTRUCTIONS_KEY to instructions)
            }
    }
}