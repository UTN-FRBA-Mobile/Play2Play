package com.p2p.presentation.truco.finalscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTrucoFinalScoreBinding =
        FragmentTrucoFinalScoreBinding::inflate

    override val viewModel: FinalScoreTrucoViewModel by viewModels()

    private lateinit var trucoFinalScoreAdapter: TrucoFinalScoreAdapter

    override fun initUI() {
        super.initUI()
        setupObservers()
        setupScoreRecycler()
        gameViewModel.stopLoading()
        gameBinding.exitButton.setOnClickListener { viewModel.exit() }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(finalScores) {
                trucoFinalScoreAdapter.results = it
                gameBinding.winner.text = resources.getString(R.string.tf_winner, it.first().player)
            }
        }
    }

    private fun setupScoreRecycler() {
        with(gameBinding.playersScores) {
            layoutManager = LinearLayoutManager(context)
            adapter = TrucoFinalScoreAdapter().also {
                this@FinalScoreTrucoFragment.trucoFinalScoreAdapter = it
            }
        }
    }

    override fun onEvent(event: TrucoFinalScoreEvent) = when (event) {
        is EndTrucoGame -> requireActivity().finish()
    }

    companion object {
        fun newInstance() = FinalScoreTrucoFragment()
    }
}
