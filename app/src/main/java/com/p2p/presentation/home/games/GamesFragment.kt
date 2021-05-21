package com.p2p.presentation.home.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentGamesBinding
import com.p2p.presentation.base.BaseFragment
import com.p2p.presentation.extensions.clearAndAppend

class GamesFragment : BaseFragment<FragmentGamesBinding, Unit, GamesViewModel>() {

    override val viewModel: GamesViewModel by viewModels { GamesViewModelFactory(requireContext()) }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGamesBinding = FragmentGamesBinding::inflate

    private lateinit var adapter: GamesAdapter

    override fun initUI() {
        setupGamesRecycler()
        binding.createButton.setOnClickListener { viewModel.createGame(getUserName()) }
        binding.joinButton.setOnClickListener { viewModel.joinGame(getUserName()) }
    }

    override fun setupObservers() = with(viewModel) {
        games.observe(viewLifecycleOwner) { adapter.games = it }
        userName.observe(viewLifecycleOwner) { binding.userNameInput.clearAndAppend(it) }
        createButtonEnabled.observe(viewLifecycleOwner) { binding.createButton.isEnabled = it }
    }

    private fun setupGamesRecycler() = with(binding.gamesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = GamesAdapter(viewModel::selectGame).also {
            this@GamesFragment.adapter = it
        }
    }

    private fun getUserName() = binding.userNameInput.text?.toString()

    companion object {
        /** Create a new instance of the [GamesFragment]. */
        fun newInstance() = GamesFragment()
    }
}