package ar.com.play2play.data.tuttifrutti
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

/** This interface brings the capacity to show all categories from the game. */
interface CategoriesSource {

    /** Returns all the categories available to select. */
    fun getAll(): List<Category>
}
