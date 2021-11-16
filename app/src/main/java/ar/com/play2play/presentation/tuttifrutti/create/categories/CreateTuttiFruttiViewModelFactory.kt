package ar.com.play2play.presentation.tuttifrutti.create.categories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.com.play2play.data.tuttifrutti.TuttiFruttiRepository
import ar.com.play2play.framework.CategoriesLocalResourcesSource

class CreateTuttiFruttiViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(TuttiFruttiRepository::class.java)
        .newInstance(TuttiFruttiRepository(CategoriesLocalResourcesSource(context)))

}
