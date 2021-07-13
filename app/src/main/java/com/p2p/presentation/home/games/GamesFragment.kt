package com.p2p.presentation.home.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentGamesBinding
import com.p2p.framework.ApplicationSharer
import com.p2p.presentation.base.BaseFragment
import com.p2p.presentation.bluetooth.TurnOnBluetoothActivity
import com.p2p.presentation.extensions.clearAndAppend
import com.p2p.presentation.home.HomeActivity.Companion.GAME_REQUEST_CODE
import com.p2p.presentation.home.join.JoinGamesBottomSheetFragment
import com.p2p.presentation.impostor.ImpostorActivity
import com.p2p.presentation.tuttifrutti.TuttiFruttiActivity

class GamesFragment : BaseFragment<FragmentGamesBinding, GamesEvents, GamesViewModel>() {

    override val viewModel: GamesViewModel by viewModels { GamesViewModelFactory(requireContext()) }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGamesBinding =
        FragmentGamesBinding::inflate

    private lateinit var adapter: GamesAdapter

    override fun initUI() {
        setupGamesRecycler()
        binding.createButton.isEnabled = false
        binding.createButton.setOnClickListener { viewModel.createGame(getUserName()) }
        binding.joinButton.setOnClickListener { viewModel.joinGame(getUserName()) }
        binding.shareButton.setOnClickListener { ApplicationSharer.share(requireActivity()) }
    }

    override fun setupObservers() = with(viewModel) {
        observe(games) { adapter.games = it }
        observe(userName) { binding.userNameInput.clearAndAppend(it) }
        observe(createButtonEnabled) { binding.createButton.isEnabled = it }
    }

    override fun onEvent(event: GamesEvents) = when (event) {
        GoToCreateTuttiFrutti -> TuttiFruttiActivity.startCreate(requireActivity(), GAME_REQUEST_CODE)
        GoToCreateImpostor -> ImpostorActivity.startCreate(requireActivity(), GAME_REQUEST_CODE)
        JoinGame -> JoinGamesBottomSheetFragment.newInstance().show(parentFragmentManager, null)
        TurnOnBluetooth -> TurnOnBluetoothActivity.start(requireContext())
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