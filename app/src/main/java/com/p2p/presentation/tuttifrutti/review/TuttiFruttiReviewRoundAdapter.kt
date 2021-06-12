package com.p2p.presentation.tuttifrutti.create.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewCategoryItemBinding
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.utils.isEven

// (Ver en Play tutti frutti fragment) observo el actual round obtengo Ronda número y letra para setear en la vista
// Fragment inicializa view model, observa lista de finished round y setea esa lista al adapter
// Agarrar lista finish round del view model, después limpiarla
// Seteo lista en el View model

// Dependiendo de la posición de la vista decido si es un título o un item , 1 vista para el título y N vistas por cantidad de jugadores

// En el OnClick de los puntajes voy agregando o restando a un objeto que me guarde el estado (mapa review points)
// Cuando toco continue se manda ese objeto o un resumen de ese objeto

// Lista de puntos por persona, mando mensaje al viewmodel de finishReview
// Y el viewmodel va a mandar mensaje a los demás de iniciar siguiente ronda (esto esta en otra tarea)

/** The adapter used to show the list of round reviews. */
class TuttiFruttiReviewRoundAdapter(
    private val finishedRoundInfo: List<FinishedRoundInfo>,
    private val finishedRoundPointsInfo: MutableLiveData<List<FinishedRoundPointsInfo>>) :
    RecyclerView.Adapter<TuttiFruttiReviewRoundAdapter.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position) // ver cuenta para ver si la posicion de la vista es de tipo titulo o de tipo palabra/jugador.
        // dependiendo de la posicion en la que estoy tengo que devolver uno u otro.
        // ej. posicion 0 titulo, posicion 1 a 5 palabra/jugador, posicion 6 titulo, y asi.... ver que cuenta tengo que hacer, modulo?
    }

    // este metodo se llama con el view type que declare arriba, asi se que vista tengo que inflar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // ver que vista estoy bindeando, segun la vista que tenga (titulo, o palabra/persona) tengo que mostrar sus elemntos
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size // cant jugadores * cant categorias

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackground else R.color.wild_sand

    //
    inner class ViewHolder(private val binding: ViewCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [category] into the view. */
        fun bind(category: Category, position: Int) = with(binding) {
            item.text = category
            categoryItem.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(position)
                )
            )

            item.isChecked = selectedCategories?.contains(category) ?: false
        }
    }
}


