package com.p2p.framework

import com.p2p.data.tuttifrutti.CategoriesSource
import com.p2p.presentation.tuttifrutti.create.Category

/**It is necessary to not be a data class so it can be called from the viewModelFactory as .getConstructor(CategoriesLocalResourcesSource::class.java)*/
class CategoriesLocalResourcesSource(private var categories: List<Category>) : CategoriesSource {
    override fun setCategories(categories: List<Category>) {
        this.categories = categories
    }

    override fun getCategories(): List<Category> =
        categories

}