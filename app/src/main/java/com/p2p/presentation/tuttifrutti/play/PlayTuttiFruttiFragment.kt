package com.p2p.presentation.tuttifrutti.play

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.p2p.R
import com.p2p.databinding.FragmentPlayTuttiFruttiBinding
import com.p2p.databinding.ViewPlayCategoryItemBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.tuttifrutti.GoToReview
import com.p2p.presentation.tuttifrutti.ObtainWords
import com.p2p.presentation.tuttifrutti.TuttiFruttiSpecificGameEvent
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.presentation.tuttifrutti.review.TuttiFruttiReviewFragment
import com.p2p.utils.text

class PlayTuttiFruttiFragment : BaseGameFragment<
        FragmentPlayTuttiFruttiBinding,
        TuttiFruttiPlayingEvents,
        PlayTuttiFruttiViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: PlayTuttiFruttiViewModel by viewModels()

    private val categoriesInputs: MutableMap<Category, TextInputLayout> = mutableMapOf()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTuttiFruttiBinding =
        FragmentPlayTuttiFruttiBinding::inflate

    override fun initValues() {
        gameViewModel.startRound()
    }

    override fun initUI() {
        super.initUI()
        with(gameBinding) {
            setUpCategoriesList(categoriesList)
            finishRoundButton.setOnClickListener { viewModel.tryToFinishRound(getCategoriesValues()) }
        }
    }

    private fun setUpCategoriesList(list: LinearLayout) = with(gameViewModel) {
        categoriesToPlay.observe(viewLifecycleOwner) {
            it.map { category ->
                categoriesInputs[category] =
                    ViewPlayCategoryItemBinding.inflate(layoutInflater, list, true).run {
                        input.hint = category
                        textField.setOnFocusChangeListener { _, _ ->
                            input.error = null
                            input.isErrorEnabled = false
                        }
                        root
                }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            singleTimeEvent.observe(viewLifecycleOwner) { onGameEvent(it) }
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
        }
    }

    override fun onEvent(event: TuttiFruttiPlayingEvents) = when (event) {
        FinishRound -> gameViewModel.enoughForMeEnoughForAll()
        ShowInvalidInputs -> markErrors()
    }

    private fun onGameEvent(event: GameEvent) {
        when (event) {
            ObtainWords -> gameViewModel.sendWords(getCategoriesValues())
            is GoToReview -> addFragment(
                TuttiFruttiReviewFragment.newInstance(),
                shouldAddToBackStack = false
            )
            else -> Unit
        }
    }

    private fun markErrors() {
        categoriesInputs.values.filter { it.text().isBlank() }.forEach {
            it.error = resources.getString(R.string.tf_validation_error)
        }
    }

    private fun getCategoriesValues() = categoriesInputs.mapValues { it.value.text() }

    companion object {

        /** Create a new instance of the [PlayTuttiFruttiFragment]. */
        fun newInstance() = PlayTuttiFruttiFragment()
    }
}
