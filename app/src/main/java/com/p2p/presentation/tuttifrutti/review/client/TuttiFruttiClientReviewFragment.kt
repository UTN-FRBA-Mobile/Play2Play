package com.p2p.presentation.tuttifrutti.review.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.R
import com.p2p.databinding.FragmentReviewTuttiFruttiBinding
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.base.NoViewModel
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.utils.fromHtml

class TuttiFruttiClientReviewFragment : BaseGameFragment<
        FragmentReviewTuttiFruttiBinding,
        Any,
        NoViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel by viewModels<NoViewModel>()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewTuttiFruttiBinding =
        FragmentReviewTuttiFruttiBinding::inflate

    private lateinit var tuttiFruttiClientReviewRoundAdapter: TuttiFruttiClientReviewRoundAdapter

    override fun initUI() {
        super.initUI()
        gameViewModel.stopLoading()
        setupReviewCategoriesRecycler()
    }

    private fun setupReviewCategoriesRecycler() = with(gameBinding.reviewCategoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = TuttiFruttiClientReviewRoundAdapter().also {
            this@TuttiFruttiClientReviewFragment.tuttiFruttiClientReviewRoundAdapter = it
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
                tuttiFruttiClientReviewRoundAdapter.finishedRoundInfo = finishedRoundInfo.toList()
                gameBinding.enoughPlayer.text = resources
                    .getString(R.string.tf_enough_player, finishedRoundInfo.first { it.saidEnough }.player)
                    .fromHtml()
            }
        }

        super.setupObservers()
    }

    companion object {

        /** Create a new instance of the [TuttiFruttiClientReviewFragment]. */
        fun newInstance() = TuttiFruttiClientReviewFragment()
    }
}
