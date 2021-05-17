package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.base.BaseFragment

class CreateTuttiFruttiFragment :
    BaseFragment<FragmentCreateTuttiFruttiBinding, TuttiFruttiCategoriesEvents, CreateTuttiFruttiViewModel>() {

    override val viewModel: CreateTuttiFruttiViewModel by viewModels {
        CreateTuttiFruttiViewModelFactory(
            requireContext()
        )
    }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTuttiFruttiBinding =
        FragmentCreateTuttiFruttiBinding::inflate

    private lateinit var categoriesAadapter: CategoriesAdapter
    private lateinit var tuttiFruttiSelectedCategoriesAdapter: TuttiFruttiSelectedCategoriesAdapter


    override fun initUI() {
        setupCategoriesRecycler()
        setupCategoriesSelectedRecycler()
        binding.continueButton.setOnClickListener { viewModel.next() }
    }

    override fun setupObservers() = with(viewModel) {
        allCategories.observe(viewLifecycleOwner) { categoriesAadapter.categories = it }
        selectedCategories.observe(viewLifecycleOwner) {
            categoriesAadapter.selectedCategories = it
            tuttiFruttiSelectedCategoriesAdapter.selectedCategories = it
        }
        continueButtonEnabled.observe(viewLifecycleOwner) { binding.continueButton.isEnabled = it }
    }


    open override fun onEvent(event: TuttiFruttiCategoriesEvents) = when (event) {
        //TODO call next step of creating game
        ContinueCreatingGame -> {
        }
    }

    private fun setupCategoriesRecycler() = with(binding.categoriesRecycle) {
        layoutManager = LinearLayoutManager(context)
        adapter = CategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.categoriesAadapter = it
        }
    }


    private fun setupCategoriesSelectedRecycler() = with(binding.categoriesSelectedRecycle) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = TuttiFruttiSelectedCategoriesAdapter(viewModel::deleteCategoryFromFooter).also {
            this@CreateTuttiFruttiFragment.tuttiFruttiSelectedCategoriesAdapter = it
        }
    }


    companion object {

        /** Create a new instance of the [CreateTuttiFruttiFragment]. */
        fun newInstance() = CreateTuttiFruttiFragment()
    }
}