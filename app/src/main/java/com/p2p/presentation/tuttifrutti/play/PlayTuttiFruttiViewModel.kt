package com.p2p.presentation.tuttifrutti.play

import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category

class PlayTuttiFruttiViewModel :
    BaseViewModel<TuttiFruttiPlayingEvents>() {

    fun onFinishRound(categoriesWithValues: Map<Category, String>) {
        val event = if (allCategoriesAreFilled(categoriesWithValues)) {
            FinishRound(categoriesWithValues)
        } else {
            InvalidInputs
        }
        dispatchSingleTimeEvent(event)
    }

    private fun allCategoriesAreFilled(categoryWithValues: Map<Category, String>): Boolean =
        categoryWithValues.values.all { it.isNotBlank() }


}
