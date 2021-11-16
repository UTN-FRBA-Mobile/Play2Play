package ar.com.play2play.presentation.tuttifrutti.create.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.R
import ar.com.play2play.data.tuttifrutti.TuttiFruttiRepository
import ar.com.play2play.presentation.base.BaseViewModel
import ar.com.play2play.presentation.extensions.requireValue

class CreateTuttiFruttiViewModel(repository: TuttiFruttiRepository) :
    BaseViewModel<TuttiFruttiCategoriesEvents>() {

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
    private val _selectedCategories = MutableLiveData(emptySet<Category>())
    val selectedCategories: LiveData<Set<Category>> = _selectedCategories

    init {
        _allCategories.value = repository.allCategories()
        _continueButtonEnabled.value = false
    }

    /** Changes the [Category] selection */
    fun changeCategorySelection(
        category: Category,
        shouldSelect: Boolean = !selectedCategories.requireValue().contains(category)
    ) {
        if (shouldSelect) {
            _selectedCategories.value = selectedCategories.requireValue() + category
        } else {
            _selectedCategories.value = selectedCategories.requireValue() - category
        }
        _continueButtonEnabled.value = categoriesCountIsValid()
    }

    /** Next view to show when Continue button is pressed. */
    fun continueToNextScreen() {
        if (!validateCategoriesCount()) return
        dispatchSingleTimeEvent(GoToSelectRounds(selectedCategories.requireValue().toList()))
    }

    private fun validateCategoriesCount(): Boolean {
        return if (!categoriesCountIsValid()) {
            dispatchMessage(
                textRes = R.string.games_tutti_frutti,
                type = MessageData.Type.ERROR
            )
            false
        } else {
            true
        }
    }

    fun categoriesCountIsValid() =
        selectedCategories.value?.size?.let { it >= CATEGORIES_VALID_THRESHOLD } ?: false

    companion object {
        const val CATEGORIES_VALID_THRESHOLD: Int = 5
    }
}
