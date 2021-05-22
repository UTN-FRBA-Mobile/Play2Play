package com.p2p.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentRoundsNumberBinding
import com.p2p.presentation.base.BaseDialogFragment
import com.p2p.presentation.home.games.GamesEvents
import com.p2p.presentation.home.games.GoToSelectRounds
import com.p2p.presentation.home.games.GoToTuttiFruttiLobby

class RoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, GamesEvents, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding = FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        binding.number.text = DEFAULT_ROUNDS_NUMBER.toString()
        binding.arrowRight.setOnClickListener { viewModel.increase(getNumber()) }
        binding.arrowLeft.setOnClickListener { viewModel.decrease(getNumber()) }
        binding.createButton.setOnClickListener { viewModel.continueCreatingGame() }
    }

    override fun setupObservers() = with(viewModel) {
        roundsNumber.observe(viewLifecycleOwner) { binding.number.text = it.toString()  }
    }

    override fun onEvent(event: GamesEvents) = when (event) {
        // TODO: GoToTuttiFruttiLobby ->
        else -> Unit
    }

    private fun getNumber(): Int = binding.number.text.toString().toInt()

    companion object {
        const val DEFAULT_ROUNDS_NUMBER = 5
    }

}
