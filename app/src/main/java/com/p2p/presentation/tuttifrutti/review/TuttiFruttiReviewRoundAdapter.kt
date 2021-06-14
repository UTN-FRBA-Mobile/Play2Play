package com.p2p.presentation.tuttifrutti.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.p2p.R
import com.p2p.databinding.ViewReviewCategoryItemBinding
import com.p2p.databinding.ViewReviewWordItemBinding
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.utils.isEven

// En el OnClick de los puntajes voy agregando o restando a un objeto que me guarde el estado (mapa review points)
// Cuando toco continue se manda ese objeto o un resumen de ese objeto

// Lista de puntos por persona, mando mensaje al viewmodel de finishReview
// Y el viewmodel va a mandar mensaje a los demás de iniciar siguiente ronda (esto esta en otra tarea)

/** The adapter used to show the list of round reviews. */
class TuttiFruttiReviewRoundAdapter(
    private val finishedRoundInfo: List<FinishedRoundInfo>,
    private val finishedRoundPointsInfo: LiveData<List<FinishedRoundPointsInfo>>) :
    RecyclerView.Adapter<TuttiFruttiReviewRoundAdapter.RecyclerViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        // If the the modulus number of players plus one is zero, then we get the category title view
        return position % (finishedRoundInfo.count() + 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        if (viewType == 0) {
            return ReviewCategoryViewHolder(
                ViewReviewCategoryItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ReviewWordViewHolder(
            ViewReviewWordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // ver que vista estoy bindeando, segun la vista que tenga (titulo, o palabra/persona) tengo que mostrar sus elemntos
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val viewIndex = getItemViewType(position)
        val viewHolderParams = RecyclerViewHolderParameters()
        val categoryIndex = position / (finishedRoundInfo.count() + 1)
        val viewCategory = getCategory(categoryIndex)

        if (viewIndex == 0) {
            viewHolderParams.category = viewCategory
        } else {
            viewHolderParams.player = finishedRoundInfo[viewIndex - 1].player
            viewHolderParams.word = finishedRoundInfo[viewIndex - 1].categoriesWords[viewCategory]!!
            viewHolderParams.points = finishedRoundPointsInfo.value!!.find {
                it.player == viewHolderParams.player
            }!!.wordsPoints[categoryIndex]
        }

        return holder.bind(viewHolderParams, position)
    }

    // Return the number of players multiplied by the number of categories plus the number of categories (for the titles)
    override fun getItemCount(): Int {
        val numberOfCategories = finishedRoundInfo.first().categoriesWords.count()
        return finishedRoundInfo.count() * numberOfCategories + numberOfCategories
    }

    private fun getCategory(categoryIndex: Int): Category {
        return finishedRoundInfo.first().categoriesWords.keys.toTypedArray()[categoryIndex]
    }

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackground else R.color.wild_sand

    inner class ReviewCategoryViewHolder(private val binding: ViewReviewCategoryItemBinding) : RecyclerViewHolder(binding) {
        /** Show the given [category] into the view. */
        override fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int) = with(binding) {
            categoryTitle.text = viewHolderParams.category
        }
    }

    inner class ReviewWordViewHolder(private val binding: ViewReviewWordItemBinding) : RecyclerViewHolder(binding) {
        /** Show the given [review] into the view. */
        override fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int) = with(binding) {
            playerName.text = viewHolderParams.player
            playerWord.text = viewHolderParams.word
            playerPoints.text = viewHolderParams.points.toString().padStart(2, ' ')
            reviewWordContainer.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(position)
                )
            )
        }
    }

    abstract class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(viewHolderParams : RecyclerViewHolderParameters, position: Int)
    }
}
