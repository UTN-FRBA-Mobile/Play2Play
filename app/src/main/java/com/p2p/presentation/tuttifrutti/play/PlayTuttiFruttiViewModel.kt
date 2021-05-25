package com.p2p.presentation.tuttifrutti.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.tuttifrutti.TuttiFruttiMetadata
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.tuttifrutti.create.Category

class PlayTuttiFruttiViewModel :
    BaseViewModel<TuttiFruttiPlayingEvents>() {

    val categoriesData = mutableMapOf<Category, String>()

    /** Whether the continue button is enabled or not. */
    private val _stopButtonEnabled = MutableLiveData<Boolean>()
    val stopButtonEnabled: LiveData<Boolean> = _stopButtonEnabled


    /** Whether the continue button is enabled or not. */
    private val _metadata = MutableLiveData<TuttiFruttiMetadata>()
    val metadata: LiveData<TuttiFruttiMetadata> = _metadata

    init {
        _stopButtonEnabled.value = false
    }

    fun setMetadata(metadata: TuttiFruttiMetadata) {
        _metadata.value = metadata
    }

    fun onWrittenCategory(category: Category, value: String?) {
        value?.run {
            categoriesData[category] = this
        }

        _stopButtonEnabled.value = allCategoriesAreFilled()
    }

    private fun allCategoriesAreFilled(): Boolean {
        val filledCategoriesCount = categoriesData.filter { it.value.isNotBlank() }.size
        return filledCategoriesCount == metadata.value!!.categories.size
    }


}
