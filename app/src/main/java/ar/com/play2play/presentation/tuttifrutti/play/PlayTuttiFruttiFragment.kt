package ar.com.play2play.presentation.tuttifrutti.play

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import ar.com.play2play.R
import ar.com.play2play.databinding.FragmentPlayTuttiFruttiBinding
import ar.com.play2play.databinding.ViewPlayCategoryItemBinding
import ar.com.play2play.presentation.basegame.BaseGameFragment
import ar.com.play2play.presentation.basegame.GameEvent
import ar.com.play2play.presentation.tuttifrutti.ObtainWords
import ar.com.play2play.presentation.tuttifrutti.TuttiFruttiViewModel
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category
import ar.com.play2play.utils.fromHtml
import ar.com.play2play.utils.text

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
        gameViewModel.generateNextRoundValues()
    }

    override fun initUI() {
        super.initUI()
        with(gameBinding) {
            setUpCategoriesList(categoriesList)
            finishRoundButton.setOnClickListener { viewModel.tryToFinishRound(getCategoriesValues()) }
        }
    }

    private fun setUpCategoriesList(list: LinearLayout) = with(gameViewModel) {
        observe(categoriesToPlay) {
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
            observe(singleTimeEvent) { onGameEvent(it) }
            observe(actualRound) {
                gameBinding.round.text = resources
                    .getString(R.string.tf_round, it.number, totalRounds.value)
                    .fromHtml()
                gameBinding.letter.text = resources
                    .getString(R.string.tf_letter, it.letter)
                    .fromHtml()
            }
        }
    }

    override fun onEvent(event: TuttiFruttiPlayingEvents) = when (event) {
        FinishRound -> gameViewModel.enoughForMeEnoughForAll()
        ShowInvalidInputs -> markErrors()
    }

    private fun onGameEvent(event: GameEvent) {
        when (event) {
            ObtainWords -> gameViewModel.sendWords(getCategoriesValues() as LinkedHashMap<Category, String>)
            else -> Unit
        }
    }

    private fun markErrors() {
        categoriesInputs.values.filter { it.text().isBlank() }.forEachIndexed { index, text ->
            text.error = resources.getString(R.string.tf_validation_error)
            if(index == 0) text.requestFocus()
        }
    }

    private fun getCategoriesValues() = categoriesInputs.mapValues { it.value.text() }

    companion object {

        /** Create a new instance of the [PlayTuttiFruttiFragment]. */
        fun newInstance() = PlayTuttiFruttiFragment()
    }
}
