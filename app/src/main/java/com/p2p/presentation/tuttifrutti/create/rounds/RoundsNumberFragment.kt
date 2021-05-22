package com.p2p.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentRoundsNumberBinding
import com.p2p.presentation.base.BaseDialogFragment
import com.p2p.presentation.home.games.GamesEvents

class RoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, RoundsNumberEvents, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding = FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener { viewModel.continueCreatingGame() }
    }

    override fun setupObservers() = with(viewModel) {
        roundsNumber.observe(viewLifecycleOwner) { binding.number.text = it.toString()  }
    }

    override fun onEvent(event: RoundsNumberEvents) = when (event) {
        // TODO: Go to Lobby
        GoToTuttiFruttiLobby -> Unit
    }
}
