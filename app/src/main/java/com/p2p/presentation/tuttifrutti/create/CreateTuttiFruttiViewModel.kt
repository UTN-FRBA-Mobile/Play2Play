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

    /** Selected categories for current game. */
    private val _selectedCategories = MutableLiveData(repository.getCategories())
    val selectedCategories: LiveData<MutableSet<Category>> = _selectedCategories

    /** Whether the continue button is enabled or not. */
    private val _continueButtonEnabled = MutableLiveData<Boolean>()
    val continueButtonEnabled: LiveData<Boolean> = _continueButtonEnabled

    /** The list of categories available to choose. */
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    init {
        _categories.value = Category.values().toList()
    }

    /** Add [category] to the selected list. */
    fun addCategory(category: Category) {
        _continueButtonEnabled.value = repository.categoriesCountIsValid()
        selectedCategories.value?.add(category)
        repository.addCategory(category)
    }

    /** Continues creating the game */
    fun next() {
        if (!repository.categoriesCountIsValid()) return
        dispatchSingleTimeEvent(ContinueCreatingGame)
    }
}
