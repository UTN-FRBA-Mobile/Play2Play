package com.p2p.presentation.truco.finalscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.R
import com.p2p.databinding.FragmentTrucoFinalScoreBinding
import com.p2p.model.truco.TrucoFinalScore
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

    override fun initUI() {
        super.initUI()
        setupObservers()
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(finalScores) {
                gameBinding.resultText.text = getResultText(it)
                gameBinding.resultText.setTextColor(ContextCompat.getColor(requireContext(), getResultColorText(it)))
                gameBinding.resultIcon.setImageResource(getResultIcon(it))
                gameBinding.resultBackground.setBackgroundColor(ContextCompat.getColor(requireContext(), getResultBackground(it)))
                gameBinding.resultPoints.text = resources.getString(R.string.tr_points, it.first{ finalScore ->
                    finalScore.team == getPlayerTeam(it)
                }.finalScore)
                gameBinding.resultPoints.setTextColor(ContextCompat.getColor(requireContext(), getResultPointsColorText(it)))
                gameBinding.otherTeamPoints.text = resources.getString(R.string.tr_points, it.first{ finalScore ->
                    finalScore.team != getPlayerTeam(it)
                }.finalScore)
            }
        }
    }

    private fun getResultText(trucoFinalScores: List<TrucoFinalScore>): CharSequence {
        return if(gameViewModel.isPlayerInWinnerTeam(trucoFinalScores)) {
            resources.getString(R.string.tr_winner)
        } else {
            resources.getString(R.string.tr_loser)
        }
    }

    private fun getResultColorText(trucoFinalScores: List<TrucoFinalScore>): Int {
        return if(gameViewModel.isPlayerInWinnerTeam(trucoFinalScores)) {
            R.color.coral
        } else {
            R.color.colorPrimary
        }
    }

    private fun getResultPointsColorText(trucoFinalScores: List<TrucoFinalScore>): Int {
        return if(gameViewModel.isPlayerInWinnerTeam(trucoFinalScores)) {
            R.color.colorSecondary
        } else {
            R.color.green_eden
        }
    }


    private fun getResultIcon(trucoFinalScores: List<TrucoFinalScore>): Int {
        return if(gameViewModel.isPlayerInWinnerTeam(trucoFinalScores)) {
            R.drawable.ic_crown
        } else {
            R.drawable.ic_broken_heart
        }
    }

    private fun getResultBackground(trucoFinalScores: List<TrucoFinalScore>): Int {
        return if(gameViewModel.isPlayerInWinnerTeam(trucoFinalScores)) {
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
