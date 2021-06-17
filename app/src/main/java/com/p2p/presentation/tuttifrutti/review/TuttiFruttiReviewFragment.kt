package com.p2p.presentation.tuttifrutti.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.R
import com.p2p.databinding.FragmentReviewTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiReviewFragment : BaseGameFragment<
        FragmentReviewTuttiFruttiBinding,
        TuttiFruttiReviewEvents,
        TuttiFruttiReviewViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: TuttiFruttiReviewViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewTuttiFruttiBinding =
        FragmentReviewTuttiFruttiBinding::inflate

    private lateinit var tuttiFruttiReviewRoundAdapter: TuttiFruttiReviewRoundAdapter


    override fun initValues() {
        viewModel.initializeBaseRoundPoints(
            gameViewModel.actualRound.value!!,
            gameViewModel.finishedRoundInfos.value!!
        )
    }

    override fun initUI() {
        super.initUI()
        setupReviewCategoriesRecycler()
        gameBinding.finishReviewButton.setOnClickListener { viewModel.sendRoundPoints() }
    }

    private fun setupReviewCategoriesRecycler() = with(gameBinding.reviewCategoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiReviewRoundAdapter(viewModel::onChangeRoundPoints).also {
            this@TuttiFruttiReviewFragment.tuttiFruttiReviewRoundAdapter = it
        }
    }

    //viewModel.finishedRoundInfo, viewModel.finishedRoundPointsInfo
    override fun setupObservers() {
        with(gameViewModel) {
            actualRound.observe(viewLifecycleOwner) {
                gameBinding.actualRound.text = resources.getString(R.string.tf_actual_round, it.number)
                gameBinding.totalRounds.text = resources.getString(R.string.tf_total_rounds, totalRounds.value)
                gameBinding.letter.text = resources.getString(R.string.tf_letter, it.letter)
            }
            finishedRoundInfos.observe(viewLifecycleOwner) { finishedRoundInfo ->
                tuttiFruttiReviewRoundAdapter.finishedRoundInfo = finishedRoundInfo
                gameBinding.enoughPlayer.text =
                    resources.getString(R.string.tf_enough_player, finishedRoundInfo.find { it.saidEnough }!!.player)
            }
        }
        with(viewModel) {
            finishedRoundPointsInfo.observe(viewLifecycleOwner) {
                tuttiFruttiReviewRoundAdapter.finishedRoundPointsInfo = it
            }
        }

        super.setupObservers()
    }

    companion object {

        /** Create a new instance of the [TuttiFruttiReviewFragment]. */
        fun newInstance() = TuttiFruttiReviewFragment()
    }
}
