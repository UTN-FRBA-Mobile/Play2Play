package com.p2p.presentation.tuttifrutti.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.model.tuttifrutti.TuttiFruttiInfo
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.tuttifrutti.create.Category

class PlayTuttiFruttiViewModel :
    BaseViewModel<TuttiFruttiPlayingEvents>() {

    val categoriesData = mutableMapOf<Category, String>()

    /** Whether the stop button is enabled or not. */
    private val _stopButtonEnabled = MutableLiveData<Boolean>()
    val stopButtonEnabled: LiveData<Boolean> = _stopButtonEnabled

    private val _info: TuttiFruttiInfo by lazy {

    }

    init {
        _stopButtonEnabled.value = false
    }

    fun onWrittenCategory(category: Category, value: String?) {
        value?.run {
            categoriesData[category] = this
        }

        _stopButtonEnabled.value = allCategoriesAreFilled()
    }

    private fun allCategoriesAreFilled(): Boolean {
        val filledCategoriesCount = categoriesData.filter { it.value.isNotBlank() }.size
        return filledCategoriesCount == metadata.categories.size
    }


}
