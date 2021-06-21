package com.p2p.presentation.tuttifrutti.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
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
        gameViewModel.stopLoading()
        setupReviewCategoriesRecycler()
        gameBinding.finishReviewButton.setOnClickListener { viewModel.sendRoundPoints() }
    }

    private fun setupReviewCategoriesRecycler() = with(gameBinding.reviewCategoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiReviewRoundAdapter(viewModel::onChangeRoundPoints).also {
            this@TuttiFruttiReviewFragment.tuttiFruttiReviewRoundAdapter = it
        }
    }

    override fun setupObservers() {
        with(gameViewModel) {
            actualRound.observe(viewLifecycleOwner) {
                gameBinding.round.text = HtmlCompat.fromHtml(
                    resources.getString(R.string.tf_round, it.number, totalRounds.value),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
                gameBinding.letter.text = HtmlCompat.fromHtml(
                    resources.getString(R.string.tf_letter, it.letter),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            finishedRoundInfos.observe(viewLifecycleOwner) { finishedRoundInfo ->
                tuttiFruttiReviewRoundAdapter.finishedRoundInfo = finishedRoundInfo
                gameBinding.enoughPlayer.text =
                    resources.getString(R.string.tf_enough_player, finishedRoundInfo.find { it.saidEnough }!!.player)
                gameBinding.enoughPlayer.text = HtmlCompat.fromHtml(
                    resources.getString(R.string.tf_enough_player, finishedRoundInfo.find { it.saidEnough }!!.player),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
        }
        with(viewModel) {
            finishedRoundPointsInfo.observe(viewLifecycleOwner) {
                tuttiFruttiReviewRoundAdapter.finishedRoundPointsInfo = it
            }
        }

        super.setupObservers()
    }

    override fun onEvent(event: TuttiFruttiReviewEvents) = when (event) {
        FinishRoundReview -> gameViewModel.setFinishedRoundPointsInfos(viewModel.finishedRoundPointsInfo.value!!)
    }

    companion object {

        /** Create a new instance of the [TuttiFruttiReviewFragment]. */
        fun newInstance() = TuttiFruttiReviewFragment()
    }
}
