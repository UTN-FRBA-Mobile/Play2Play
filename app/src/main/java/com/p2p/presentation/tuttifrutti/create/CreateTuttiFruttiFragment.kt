package com.p2p.presentation.tuttifrutti.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.base.BaseFragment

class CreateTuttiFruttiFragment : BaseFragment<FragmentCreateTuttiFruttiBinding, CategoriesEvents, CreateTuttiFruttiViewModel>() {

    override val viewModel: CreateTuttiFruttiViewModel by viewModels { CreateTuttiFruttiViewModelFactory() }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTuttiFruttiBinding = FragmentCreateTuttiFruttiBinding::inflate

    private lateinit var categoriesAadapter: CategoriesAdapter
    private lateinit var selectedCategoriesAadapter: SelectedCategoriesAdapter


    override fun initUI() {
        setupCategoriesRecycler()
        setupCategoriesSelectedRecycler()
        binding.continueButton.setOnClickListener { viewModel.next() }
    }

    override fun setupObservers() = with(viewModel) {
        allCategories.observe(viewLifecycleOwner) { categoriesAadapter.categories = it }
        selectedCategories.observe(viewLifecycleOwner) {
            categoriesAadapter.selectedCategories = it
            selectedCategoriesAadapter.selectedCategories = it
        }
        continueButtonEnabled.observe(viewLifecycleOwner) { binding.continueButton.isEnabled = it }
    }


    open override fun onEvent(event: CategoriesEvents) = when(event){
        //TODO call next step of creating game
        ContinueCreatingGame -> {}
    }

    private fun setupCategoriesRecycler() = with(binding.categoriesRecycle) {
        layoutManager = LinearLayoutManager(context)
        adapter = CategoriesAdapter(viewModel::changeCategorySelection).also {
            this@CreateTuttiFruttiFragment.categoriesAadapter = it
        }
    }


    private fun setupCategoriesSelectedRecycler() = with(binding.categoriesSelectedRecycle) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = SelectedCategoriesAdapter(viewModel::deleteCategoryFromFooter).also {
            this@CreateTuttiFruttiFragment.selectedCategoriesAadapter = it
        }
    }


    companion object {

        /** Create a new instance of the [CreateTuttiFruttiFragment]. */
        fun newInstance() = CreateTuttiFruttiFragment()
    }
}