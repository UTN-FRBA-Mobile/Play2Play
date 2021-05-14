package com.p2p.data.tuttifrutti
import com.p2p.presentation.tuttifrutti.create.Category

data class TuttiFruttiRepository(val categoriesSource: CategoriesSource){

    fun addCategory(category: Category) {
        categoriesSource.addCategory(category)
    }

    fun getCategories(): MutableSet<Category> = categoriesSource.getCategories()

    fun categoriesCountIsValid() = getCategories().size >= 5
}