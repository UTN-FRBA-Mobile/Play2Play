package ar.com.play2play.presentation.tuttifrutti.play

import ar.com.play2play.presentation.base.BaseViewModel
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

class PlayTuttiFruttiViewModel : BaseViewModel<TuttiFruttiPlayingEvents>() {

    fun tryToFinishRound(categoriesWithValues: Map<Category, String>) {
        val event = if (allCategoriesAreFilled(categoriesWithValues)) {
            FinishRound
        } else {
            ShowInvalidInputs
        }
        dispatchSingleTimeEvent(event)
    }

    private fun allCategoriesAreFilled(categoryWithValues: Map<Category, String>): Boolean =
        categoryWithValues.values.all { it.isNotBlank() }
}
