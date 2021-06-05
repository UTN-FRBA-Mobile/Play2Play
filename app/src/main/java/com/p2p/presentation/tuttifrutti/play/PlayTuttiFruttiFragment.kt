package com.p2p.presentation.tuttifrutti.play

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.p2p.R
import com.p2p.databinding.FragmentPlayTuttiFruttiBinding
import com.p2p.databinding.PlayCategoryItemBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.utils.clear
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
        gameViewModel.generateNextRoundValues()
    }

    override fun initUI() {
        super.initUI()
        with(gameBinding) {
            setUpCategoriesList(categoriesList)
            finishRoundButton.setOnClickListener {
                val values = categoriesInputs.map { it.key to it.value.text() }.toMap()
                viewModel.onFinishRound(values)
            }
        }
    }

    private fun getCategoriesTextInputs(): List<TextInputLayout> {
        val textViews = gameBinding.categoriesList
            .children
            .map { it as TextInputLayout }
        return textViews.toList()
    }

    private fun setUpCategoriesList(list: LinearLayout) = with(gameViewModel) {
        selectedCategories.observe(viewLifecycleOwner) {
            it.map { category ->
                with(PlayCategoryItemBinding.inflate(layoutInflater)) {
                    input.hint = category
                    categoriesInputs[category] = input
                    list.addView(this.root)
                }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            actualRound.observe(viewLifecycleOwner) {
                gameBinding.round.text =
                    resources.getString(R.string.tf_round, it.number, totalRounds.value)
                gameBinding.letter.text = resources.getString(R.string.tf_letter, it.letter)
            }
        }
    }

    override fun onEvent(event: TuttiFruttiPlayingEvents) = when (event) {
        //TODO end round and pass to next stage
        is FinishRound -> {
            clearValues()
            //TODO delete when review is done
            showSuccessMessage(event.categoriesWithValues)
            gameViewModel.finishRound(event.categoriesWithValues)
        }
        ShowInvalidInputs -> markErrors()
    }

    //TODO validation inputs
    private fun markErrors() {
        getCategoriesTextInputs().filter { it.text().isBlank() }.forEach {
            it.error = resources.getString(R.string.tf_validation_error)
        }
    }

    private fun clearValues() {
        getCategoriesTextInputs().forEach { it.clear() }
    }


    //TODO delete when review is done
    private fun showSuccessMessage(categoriesWithValues: Map<Category, String>) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Las categorias que llenaste son: \n" +
                    categoriesWithValues.map { it.key + " : " + it.value + "\n" })
            //It is positive to be shown on the right
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                // Respond to positive button press
            }
            .show()
    }


    companion object {

        /** Create a new instance of the [PlayTuttiFruttiFragment]. */
        fun newInstance() = PlayTuttiFruttiFragment()
    }
}
