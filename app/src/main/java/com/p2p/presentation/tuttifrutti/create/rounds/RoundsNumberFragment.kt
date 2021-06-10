package com.p2p.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentRoundsNumberBinding
import com.p2p.presentation.base.BaseDialogFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category

class RoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, Unit, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()

    private val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    private var roundsNumber: Int? = null

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding =
        FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener {
            gameViewModel.setTotalRounds(
                requireNotNull(roundsNumber)
                { "Round number must be defined when create button is pressed" })
            gameViewModel.goToLobby()
        }
    }

    override fun setupObservers() =
        viewModel.roundsNumber.observe(viewLifecycleOwner) {
            roundsNumber = it
            binding.number.text = it.toString()
        }


    companion object {

        /** Create a new instance of the [RoundsNumberFragment]. */
        fun newInstance() = RoundsNumberFragment()
    }
}
