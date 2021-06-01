package com.p2p.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.databinding.FragmentRoundsNumberBinding
import com.p2p.model.tuttifrutti.TuttiFruttiInfo
import com.p2p.presentation.base.BaseDialogFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.countdown.TuttiFruttiCountdownFragment
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.presentation.tuttifrutti.play.PlayTuttiFruttiFragment

class RoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, RoundsNumberEvent, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding =
        FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener {
            viewModel.continueCreatingGame()
        }
    }

    override fun setupObservers() = with(viewModel) {
        roundsNumber.observe(viewLifecycleOwner) {
            binding.number.text = it.toString()
        }
    }

    override fun onEvent(event: RoundsNumberEvent) = when (event) {
        //TODO change for actual event
        is GoToTuttiFruttiLobby -> {
            addFragment(TuttiFruttiCountdownFragment.newInstance(gameInfo(event)), false)
        }
    }

    private fun gameInfo(event: GoToTuttiFruttiLobby): TuttiFruttiInfo {
        val categories: List<Category> =
            requireNotNull(requireArguments().getParcelable(CATEGORIES_KEY))
        return TuttiFruttiInfo(event.totalRounds, categories)
    }

    companion object {

        const val CATEGORIES_KEY = "Categories"

        /** Create a new instance of the [RoundsNumberFragment] with [selectedCategories]. */
        fun newInstance(selectedCategories: List<Category>) = RoundsNumberFragment().apply {
            arguments = bundleOf(
                CATEGORIES_KEY to selectedCategories
            )
        }
    }
}
