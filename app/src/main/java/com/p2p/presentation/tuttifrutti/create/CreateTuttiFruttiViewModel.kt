package com.p2p.presentation.tuttifrutti.create

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.R
import com.p2p.data.tuttifrutti.TuttiFruttiRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.home.games.Game

class CreateTuttiFruttiViewModel(val repository: TuttiFruttiRepository) : BaseViewModel<CategoriesEvents>() {


    /** The list of categories available to select. */
    private val _allCategories = MutableLiveData<List<Category>>()
    val allCategories: LiveData<List<Category>> = _allCategories

    /** Whether the continue button is enabled or not. */
    private val _continueButtonEnabled = MutableLiveData<Boolean>()
    val continueButtonEnabled: LiveData<Boolean> = _continueButtonEnabled

    /** The current selected categories.
     * Necessary to be a LiveData so the recyclerView can be notified and change the opacity of the button,
     * if i only add the category to the repository, the view won't get notified
     * */
    private val _selectedCategories = MutableLiveData(mutableListOf<Category>())
    val selectedCategories: LiveData<MutableList<Category>> = _selectedCategories

    init {
        _allCategories.value = Category.values().toList()
    }

    /** Adds the [Category] to current game */
    fun addCategory(category: Category?) {
        _selectedCategories.value?.add(category!!)
        _continueButtonEnabled.value = categoriesCountIsValid()
    }


    /** Next view to show when Continue button is pressed. */
    fun next() {
        if (!validateCateogoriesCount()) return
        dispatchSingleTimeEvent(ContinueCreatingGame)
    }

    private fun validateCateogoriesCount(): Boolean {
        return if (!categoriesCountIsValid()) {
            dispatchMessage(MessageData(textRes = R.string.tf_categories_count_error, type = MessageData.Type.ERROR))
            false
        } else {
            repository.setCategories(selectedCategories.value!!)
            true
        }
    }


    fun categoriesCountIsValid() = selectedCategories.value?.size?.let { it >= 5 } ?: false
}
