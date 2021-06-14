package com.p2p.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentRoundsNumberBinding
import com.p2p.presentation.base.BaseDialogFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiRoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, Unit, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()

    private val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding =
        FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener {
            gameViewModel.setTotalRounds(binding.number.text.toString().toInt())
            gameViewModel.goToLobby()
        }
    }

    override fun setupObservers() =
        viewModel.roundsNumber.observe(viewLifecycleOwner) {
            binding.number.text = it.toString()
        }

    companion object {

        /** Create a new instance of the [TuttiFruttiRoundsNumberFragment]. */
        fun newInstance() = TuttiFruttiRoundsNumberFragment()
    }
}
