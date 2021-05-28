package com.p2p.framework

import android.content.Context
import com.p2p.R
import com.p2p.data.tuttifrutti.CategoriesSource
import com.p2p.presentation.tuttifrutti.create.categories.Category

/**It is necessary to not be a data class so it can be called from the viewModelFactory as .getConstructor(CategoriesLocalResourcesSource::class.java)*/
class CategoriesLocalResourcesSource(private val context: Context) : CategoriesSource {
    override fun getAll(): List<Category> =
        context.resources.getStringArray(R.array.tf_categories).toList()

}