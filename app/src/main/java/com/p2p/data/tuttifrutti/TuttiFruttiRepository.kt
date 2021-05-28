package com.p2p.data.tuttifrutti
import com.p2p.presentation.tuttifrutti.create.categories.Category

data class TuttiFruttiRepository(private val categories: CategoriesSource){

    fun allCategories(): List<Category> = categories.getAll()
}