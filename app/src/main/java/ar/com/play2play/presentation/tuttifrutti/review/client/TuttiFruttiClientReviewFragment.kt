package ar.com.play2play.presentation.tuttifrutti.review.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ar.com.play2play.R
import ar.com.play2play.databinding.FragmentClientReviewTuttiFruttiBinding
import ar.com.play2play.presentation.base.NoViewModel
import ar.com.play2play.presentation.basegame.BaseGameFragment
import ar.com.play2play.presentation.tuttifrutti.TuttiFruttiViewModel
import ar.com.play2play.utils.fromHtml

class TuttiFruttiClientReviewFragment : BaseGameFragment<
        FragmentClientReviewTuttiFruttiBinding,
        Any,
        NoViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel by viewModels<NoViewModel>()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentClientReviewTuttiFruttiBinding =
        FragmentClientReviewTuttiFruttiBinding::inflate

    private lateinit var tuttiFruttiClientReviewRoundAdapter: TuttiFruttiClientReviewRoundAdapter

    override fun initUI() {
        super.initUI()
        gameViewModel.stopLoading()
        setupClientReviewCategoriesRecycler()
    }

    private fun setupClientReviewCategoriesRecycler() = with(gameBinding.clientReviewCategoriesRecycler) {
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
