package com.p2p.presentation.tuttifrutti.finalscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.R
import com.p2p.databinding.FragmentTuttiFruttiFinalScoreBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class FinalScoreTuttiFruttiFragment : BaseGameFragment<
        FragmentTuttiFruttiFinalScoreBinding,
        TuttiFruttiFinalScoreEvent,
        FinalScoreTuttiFruttiViewModel,
        TuttiFruttiViewModel>() {
    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTuttiFruttiFinalScoreBinding =
        FragmentTuttiFruttiFinalScoreBinding::inflate

    override val viewModel: FinalScoreTuttiFruttiViewModel by viewModels()

    private lateinit var tuttiFruttiFinalScoreAdapter: TuttiFruttiFinalScoreAdapter

    override fun initUI() {
        super.initUI()
        setupObservers()
        setupScoreRecycler()
        gameBinding.exitButton.setOnClickListener { viewModel.exit() }
    }

    override fun setupObservers() = with(gameViewModel) {
        super.setupObservers()
        finishedRoundsPointsInfos.observe(viewLifecycleOwner) {
            val finalScores = it.sortedByDescending { info -> info.totalPoints }
            tuttiFruttiFinalScoreAdapter.results = finalScores
            gameBinding.winner.text = resources.getString(R.string.tf_winner, finalScores[0].player)
        }
    }

    private fun setupScoreRecycler() { with(gameBinding.playersScores) {
            layoutManager = LinearLayoutManager(context)
            adapter = TuttiFruttiFinalScoreAdapter().also {
                this@FinalScoreTuttiFruttiFragment.tuttiFruttiFinalScoreAdapter = it
            }
        }
    }

    override fun onEvent(event: TuttiFruttiFinalScoreEvent) = when(event){
        is EndTuttiFruttiGame -> requireActivity().finish()
    }

    companion object {
        fun newInstance() = FinalScoreTuttiFruttiFragment()
    }
}