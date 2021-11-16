package ar.com.play2play.data.tuttifrutti
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

data class TuttiFruttiRepository(private val categories: CategoriesSource){

    fun allCategories(): List<Category> = categories.getAll()
}