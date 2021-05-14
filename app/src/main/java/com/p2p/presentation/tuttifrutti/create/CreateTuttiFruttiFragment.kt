package com.p2p.presentation.tuttifrutti.create

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.base.BaseFragment
import com.p2p.presentation.home.games.GoToCreateTuttiFrutti
import kotlin.math.log

class CreateTuttiFruttiFragment : BaseFragment<FragmentCreateTuttiFruttiBinding, CategoriesEvents, CreateTuttiFruttiViewModel>() {

    override val viewModel: CreateTuttiFruttiViewModel by viewModels { CreateTuttiFruttiViewModelFactory() }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTuttiFruttiBinding = FragmentCreateTuttiFruttiBinding::inflate

    private lateinit var adapter: CategoriesAdapter

    override fun initUI() {
        setupCategoriesRecycler()
        binding.continueButton.setOnClickListener { viewModel.next() }
    }

    override fun setupObservers() = with(viewModel) {
        selectedCategories.observe(viewLifecycleOwner) { adapter.categories = it }
        userName.observe(viewLifecycleOwner) { binding.userNameInput.clearAndAppend(it) }
        createButtonEnabled.observe(viewLifecycleOwner) { binding.createButton.isEnabled = it }
    }


    override protected open fun onEvent(event: CategoriesEvents) = when(event){
        //TODO call next step of creating game
        ContinueCreatingGame -> {}
    }

    private fun setupCategoriesRecycler() = with(binding.categoriesRecycle) {
        layoutManager = LinearLayoutManager(context)
        adapter = CategoriesAdapter(viewModel::addCategory).also {
            this@CreateTuttiFruttiFragment.adapter = it
        }
    }

    private fun getUserName() = binding.userNameInput.text?.toString()

    companion object {

        /** Create a new instance of the [CreateTuttiFruttiFragment]. */
        fun newInstance() = CreateTuttiFruttiFragment()
    }
}