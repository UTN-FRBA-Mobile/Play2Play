package ar.com.play2play.presentation.truco.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.databinding.FragmentCreateTrucoBinding
import ar.com.play2play.presentation.basegame.BaseGameFragment
import ar.com.play2play.presentation.truco.TrucoViewModel
import ar.com.play2play.presentation.truco.create.points.CreateTrucoPointsFragment
import ar.com.play2play.presentation.tuttifrutti.create.categories.GoToSelectRounds
import ar.com.play2play.presentation.tuttifrutti.create.rounds.TuttiFruttiRoundsNumberFragment

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