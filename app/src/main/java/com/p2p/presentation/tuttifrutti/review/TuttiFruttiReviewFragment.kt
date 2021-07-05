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
import com.p2p.utils.fromHtml

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
        observe(gameViewModel.actualRound) { viewModel.setInitialActualRound(it) }
        observe(gameViewModel.finishedRoundInfos) { viewModel.setInitialFinishedRoundInfos(it) }
    }

    override fun initUI() {
        super.initUI()
        gameViewModel.stopLoading()
        setupReviewCategoriesRecycler()
        gameBinding.finishReviewButton.setOnClickListener { viewModel.sendRoundPoints() }
    }

    private fun setupReviewCategoriesRecycler() = with(gameBinding.reviewCategoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiReviewRoundAdapter(viewModel::onAddRoundPoints, viewModel::onSubstractRoundPoints).also {
            this@TuttiFruttiReviewFragment.tuttiFruttiReviewRoundAdapter = it
        }
    }

    override fun setupObservers() {
        with(gameViewModel) {
            observe(actualRound) {
                gameBinding.round.text = resources
                    .getString(R.string.tf_round, it.number, totalRounds.value)
                    .fromHtml()
                gameBinding.letter.text = resources
                    .getString(R.string.tf_letter, it.letter)
                    .fromHtml()
            }
            observe(finishedRoundInfos) { finishedRoundInfo ->
                tuttiFruttiReviewRoundAdapter.finishedRoundInfo = finishedRoundInfo
                gameBinding.enoughPlayer.text = resources
                    .getString(R.string.tf_enough_player, finishedRoundInfo.first { it.saidEnough }.player)
                    .fromHtml()
            }
        }
        with(viewModel) {
            observe(finishedRoundPointsInfo) {
                tuttiFruttiReviewRoundAdapter.finishedRoundPointsInfo = it
            }
        }

        super.setupObservers()
    }

    override fun onEvent(event: TuttiFruttiReviewEvents) = when (event) {
        is FinishRoundReview -> {
            gameViewModel.setFinishedRoundPointsInfos(event.finishedRoundPointsInfo)
            gameViewModel.startRoundOrFinishGame()
        }
    }

    companion object {

        /** Create a new instance of the [TuttiFruttiReviewFragment]. */
        fun newInstance() = TuttiFruttiReviewFragment()
    }
}
