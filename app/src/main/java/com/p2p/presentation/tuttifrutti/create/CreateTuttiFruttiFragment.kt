package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.R
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.base.BaseGameFragment
import com.p2p.presentation.home.games.Game

class CreateTuttiFruttiFragment(instructions: String) :
    BaseGameFragment<FragmentCreateTuttiFruttiBinding, TuttiFruttiCategoriesEvents, CreateTuttiFruttiViewModel>(instructions) {

    override val viewModel: CreateTuttiFruttiViewModel by viewModels {
        CreateTuttiFruttiViewModelFactory(
            requireContext()
        )
    }
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTuttiFruttiBinding =
        FragmentCreateTuttiFruttiBinding::inflate

    override val gameData = Game.TUTTI_FRUTTI

    private lateinit var tuttiFruttiCategoriesAadapter: TuttiFruttiCategoriesAdapter
    private lateinit var tuttiFruttiSelectedCategoriesAdapter: TuttiFruttiSelectedCategoriesAdapter


    override fun initGameUI() {
        setupCategoriesRecycler()
        setupCategoriesSelectedRecycler()
        gameBinding.continueButton.setOnClickListener { viewModel.continueToNextScreen() }
    }

    override fun setupObservers() = with(viewModel) {
        allCategories.observe(viewLifecycleOwner) { tuttiFruttiCategoriesAadapter.categories = it }
        selectedCategories.observe(viewLifecycleOwner) {
            tuttiFruttiCategoriesAadapter.selectedCategories = it
            tuttiFruttiSelectedCategoriesAdapter.selectedCategories = it
        }
        continueButtonEnabled.observe(viewLifecycleOwner) { gameBinding.continueButton.isEnabled = it }
    }


    open override fun onEvent(event: TuttiFruttiCategoriesEvents) = when (event) {
        //TODO call next step of creating game
        ContinueCreatingGame -> {
        }
    }

    private fun setupCategoriesRecycler() = with(gameBinding.categoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiCategoriesAadapter = it
        }
    }


    private fun setupCategoriesSelectedRecycler() = with(gameBinding.categoriesSelectedRecycler) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = TuttiFruttiSelectedCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiSelectedCategoriesAdapter = it
        }
    }

    companion object {

        /** Create a new instance of the [CreateTuttiFruttiFragment]. */
        fun newInstance(instructions: String) = CreateTuttiFruttiFragment(instructions)
    }
}