package com.p2p.presentation.tuttifrutti.create.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.base.BaseFragment
import com.p2p.presentation.home.games.GoToSelectRounds
import com.p2p.presentation.home.games.GamesEvents
import com.p2p.presentation.tuttifrutti.create.rounds.RoundsNumberFragment

class CreateTuttiFruttiFragment :
        BaseFragment<FragmentCreateTuttiFruttiBinding, GamesEvents, CreateTuttiFruttiViewModel>() {

    override val viewModel: CreateTuttiFruttiViewModel by viewModels {
        CreateTuttiFruttiViewModelFactory(
                requireContext()
        )
    }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTuttiFruttiBinding =
            FragmentCreateTuttiFruttiBinding::inflate

    private lateinit var tuttiFruttiCategoriesAadapter: TuttiFruttiCategoriesAdapter
    private lateinit var tuttiFruttiSelectedCategoriesAdapter: TuttiFruttiSelectedCategoriesAdapter

    override fun initUI() {
        setupCategoriesRecycler()
        setupCategoriesSelectedRecycler()
        binding.continueButton.setOnClickListener { viewModel.continueToNextScreen() }
    }

    override fun setupObservers() = with(viewModel) {
        allCategories.observe(viewLifecycleOwner) { tuttiFruttiCategoriesAadapter.categories = it }
        selectedCategories.observe(viewLifecycleOwner) {
            tuttiFruttiCategoriesAadapter.selectedCategories = it
            tuttiFruttiSelectedCategoriesAdapter.selectedCategories = it
        }
        continueButtonEnabled.observe(viewLifecycleOwner) { binding.continueButton.isEnabled = it }
    }


    override fun onEvent(event: GamesEvents) = when (event) {
        GoToSelectRounds -> RoundsNumberFragment().show(childFragmentManager, "RoundsNumberDialog")
        else -> Unit
    }

    private fun setupCategoriesRecycler() = with(binding.categoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiCategoriesAadapter = it
        }
    }

    private fun setupCategoriesSelectedRecycler() = with(binding.categoriesSelectedRecycler) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = TuttiFruttiSelectedCategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiSelectedCategoriesAdapter = it
        }
    }

    companion object {

        /** Create a new instance of the [CreateTuttiFruttiFragment]. */
        fun newInstance() = CreateTuttiFruttiFragment()
    }
}