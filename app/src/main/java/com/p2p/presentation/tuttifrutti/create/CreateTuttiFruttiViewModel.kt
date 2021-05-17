package com.p2p.presentation.tuttifrutti.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.R
import com.p2p.data.tuttifrutti.TuttiFruttiRepository
import com.p2p.presentation.base.BaseViewModel

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
    private val _selectedCategories = MutableLiveData(listOf<Category>())
    val selectedCategories: LiveData<List<Category>> = _selectedCategories

    init {
        _allCategories.value = Category.values().toList()
    }


    /** Changes the [Category] selection */
    fun changeCategorySelection(category: Category, isSelected: Boolean) {
        if(isSelected){
            _selectedCategories.value = selectedCategories.value?.plus(category)
        }else{
            _selectedCategories.value = selectedCategories.value?.filter { it != category }
        }
        _continueButtonEnabled.value = categoriesCountIsValid()
    }

    fun deleteCategoryFromFooter(category: Category){
        changeCategorySelection(category, isSelected = false)
    }

    /** Next view to show when Continue button is pressed. */
    fun next() {
        if (!validateCategoriesCount()) return
        dispatchSingleTimeEvent(ContinueCreatingGame)
    }

    private fun validateCategoriesCount(): Boolean {
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
