package ar.com.play2play.framework

import android.content.Context
import ar.com.play2play.R
import ar.com.play2play.data.tuttifrutti.CategoriesSource
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

/**It is necessary to not be a data class so it can be called from the viewModelFactory as .getConstructor(CategoriesLocalResourcesSource::class.java)*/
class CategoriesLocalResourcesSource(private val context: Context) : CategoriesSource {
    override fun getAll(): List<Category> =
        context.resources.getStringArray(R.array.tf_categories).toList()

}