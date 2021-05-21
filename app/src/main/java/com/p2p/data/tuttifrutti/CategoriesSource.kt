package com.p2p.data.tuttifrutti
import com.p2p.presentation.tuttifrutti.create.Category

/** This interface brings the capacity to show all categories from the game. */
interface CategoriesSource {

    /** Returns all the categories available to select. */
    fun getAll(): List<Category>
}
