package com.p2p.data.tuttifrutti
import com.p2p.presentation.tuttifrutti.create.Category

data class TuttiFruttiRepository(private val gameCategories: CategoriesSource){

    fun setCategories(categories: List<Category>) {
        gameCategories.setCategories(categories)
    }

    fun getGameCategories(): List<Category> = gameCategories.getCategories()
}