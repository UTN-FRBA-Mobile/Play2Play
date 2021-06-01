package com.p2p.presentation.tuttifrutti.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.model.tuttifrutti.TuttiFruttiInfo
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category

class PlayTuttiFruttiViewModel :
    BaseViewModel<TuttiFruttiPlayingEvents>() {

    /** Categories with the values. */
    private val categoryValues: MutableMap<Category, String?> = mutableMapOf()

    fun onFocusOut(category: Category, value: String?) {
        value?.run {
            categoryValues.put(category, this)
        }
    }

    fun finishRound(gameCategories: List<Category>) {
        val event = if (allCategoriesAreFilled(gameCategories)) EndRound else InvalidInputs
        dispatchSingleTimeEvent(event)
    }

    private fun allCategoriesAreFilled(gameCategories: List<Category>): Boolean {
        val filledCategoriesCount = categoryValues.filter { it.value?.isNotBlank() ?: false }.size
        return filledCategoriesCount == gameCategories.size
    }


}
