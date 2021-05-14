package com.p2p.framework

import com.p2p.data.tuttifrutti.CategoriesSource
import com.p2p.presentation.tuttifrutti.create.Category

data class CategoriesLocalResourcesSource(private val categories: MutableSet<Category>) : CategoriesSource {

    override fun addCategory(category: Category) {
        categories.add(category)
    }

    override fun getCategories(): MutableSet<Category> = categories
}