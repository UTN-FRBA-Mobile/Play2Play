package com.p2p.presentation.truco.finalscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.databinding.FragmentTrucoFinalScoreBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.truco.TrucoViewModel

class FinalScoreTrucoFragment : BaseGameFragment<
        FragmentTrucoFinalScoreBinding,
        TrucoFinalScoreEvent,
        FinalScoreTrucoViewModel,
        TrucoViewModel>() {

    override val gameViewModel: TrucoViewModel by activityViewModels()

    override val viewModel: FinalScoreTrucoViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTrucoFinalScoreBinding =
        FragmentTrucoFinalScoreBinding::inflate

    private var resultOurScore : Int = 0
    private var resultTheirScore : Int = 0

    override fun initUI() {
        super.initUI()
        setupObservers()
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(ourScore) {
                resultOurScore = ourScore.value!!
                viewModel.setOurScore(it)
            }
            observe(theirScore) {
                resultTheirScore = theirScore.value!!
                viewModel.setTheirScore(it)
            }
        }
        observe(viewModel.isWinner) {
            gameBinding.resultText.text = getResultText(it)
            gameBinding.resultText.setTextColor(ContextCompat.getColor(requireContext(), getResultColorText(it)))
            gameBinding.resultIcon.setImageResource(getResultIcon(it))
            gameBinding.resultBackground.setBackgroundColor(ContextCompat.getColor(requireContext(), getResultBackground(it)))
            gameBinding.resultPoints.text = resources.getString(R.string.tr_points, resultOurScore)
            gameBinding.resultPoints.setTextColor(ContextCompat.getColor(requireContext(), getResultPointsColorText(it)))
            gameBinding.otherTeamPoints.text = resources.getString(R.string.tr_points, resultTheirScore)
        }
    }

    private fun getResultText(isWinner: Boolean): CharSequence {
        return if(isWinner) {
            resources.getString(R.string.tr_winner)
        } else {
            resources.getString(R.string.tr_loser)
        }
    }

    private fun getResultColorText(isWinner: Boolean): Int {
        return if(isWinner) {
            R.color.coral
        } else {
            R.color.colorPrimary
        }
    }

    private fun getResultPointsColorText(isWinner: Boolean): Int {
        return if(isWinner) {
            R.color.colorSecondary
        } else {
            R.color.green_eden
        }
    }


    private fun getResultIcon(isWinner: Boolean): Int {
        return if(isWinner) {
            R.drawable.ic_crown
        } else {
            R.drawable.ic_broken_heart
        }
    }

    private fun getResultBackground(isWinner: Boolean): Int {
        return if(isWinner) {
            R.color.colorSecondaryVariant
        } else {
            R.color.colorPrimaryVariant
        }
    }

    override fun onEvent(event: TrucoFinalScoreEvent) = when (event) {
        is EndTrucoGame -> requireActivity().finish()
    }

    companion object {
        fun newInstance() = FinalScoreTrucoFragment()
    }
}
