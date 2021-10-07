package com.p2p.presentation.truco.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentCreateTrucoBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.truco.TrucoViewModel
import com.p2p.presentation.truco.create.points.CreateTrucoPointsFragment
import com.p2p.presentation.tuttifrutti.create.categories.GoToSelectRounds
import com.p2p.presentation.tuttifrutti.create.rounds.TuttiFruttiRoundsNumberFragment

class CreateTrucoFragment : BaseGameFragment<
        FragmentCreateTrucoBinding,
        CreateTrucoEvents,
        CreateTrucoViewModel,
        TrucoViewModel>() {

    override val gameViewModel: TrucoViewModel by activityViewModels()
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTrucoBinding =
        FragmentCreateTrucoBinding::inflate

    override val viewModel: CreateTrucoViewModel by viewModels()

    override fun initUI() {
        super.initUI()
        gameBinding.createTwoPlayer.setOnClickListener {
            viewModel.continueToNextScreen(2)
        }
        gameBinding.createFourPlayer.setOnClickListener {
            viewModel.continueToNextScreen(4)
        }
    }

    override fun onEvent(event: CreateTrucoEvents) = when(event) {
        is GoToSelectPoints -> {
            gameViewModel.setTotalPlayers(event.numberOfPlayers)
            CreateTrucoPointsFragment.newInstance().show(childFragmentManager, "TrucoPointsDialog")
        }
    }

    companion object {
        /** Create a new instance of the [CreateTrucoFragment]. */
        fun newInstance() = CreateTrucoFragment()
    }
}