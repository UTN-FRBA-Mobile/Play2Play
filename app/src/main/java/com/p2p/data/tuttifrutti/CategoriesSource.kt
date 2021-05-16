package com.p2p.data.tuttifrutti
import com.p2p.presentation.tuttifrutti.create.Category

/** This interface brings the capacity to save user info into some storage. */
interface CategoriesSource {

    /** Adds categories to current game [category]. */
    fun setCategories(categories: List<Category>)

    /** Returns all the categories for the game. */
    fun getCategories(): List<Category>
}
