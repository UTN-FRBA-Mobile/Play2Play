package ar.com.play2play.presentation.tuttifrutti.review

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ar.com.play2play.R
import ar.com.play2play.model.tuttifrutti.FinishedRoundInfo
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category
import ar.com.play2play.utils.isEven

abstract class TuttiFruttiReviewRoundAdapterHelper :
    RecyclerView.Adapter<TuttiFruttiReviewRoundAdapterHelper.RecyclerViewHolder>() {

    var finishedRoundInfo = listOf<FinishedRoundInfo>()

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val itemType = getItemViewType(position)
        val viewHolderParams: RecyclerViewHolderParameters
        val categoryIndex = getCategoryIndex(position)
        val viewCategory = getCategory(categoryIndex)
        val wordViewIndex = position % (finishedRoundInfo.count() + 1) - 1

        viewHolderParams = if (itemType == TITLE_TYPE) {
            RecyclerViewHolderParameters(category = viewCategory)
        } else {
            val wordViewPlayer = finishedRoundInfo[wordViewIndex].player
            val wordViewWord = finishedRoundInfo[wordViewIndex].categoriesWords.getValue(viewCategory)
            wordViewHolderParameters(wordViewPlayer, wordViewWord, categoryIndex)
        }

        return holder.bind(viewHolderParams, position)
    }

    abstract fun wordViewHolderParameters(
        wordViewPlayer: String,
        wordViewWord: String,
        categoryIndex: Int
    ): RecyclerViewHolderParameters

    private fun getCategory(categoryIndex: Int): Category {
        return finishedRoundInfo.first().categoriesWords.toList()[categoryIndex].first
    }

    fun getCategoryIndex(actualPosition: Int) = actualPosition / (finishedRoundInfo.count() + 1)

    abstract class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int)
    }

    // Return the number of players multiplied by the number of categories plus the number of categories (for the titles)
    override fun getItemCount() = finishedRoundInfo.count() * numberOfCategories() + numberOfCategories()

    private fun numberOfCategories() = finishedRoundInfo.first().categoriesWords.count()

    fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.wild_sand else R.color.colorBackground

    override fun getItemViewType(position: Int): Int {
        // If the the modulus number of players plus one is zero, then we get the category title view
        return if (position % (finishedRoundInfo.count() + 1) == 0) TITLE_TYPE else WORD_TYPE
    }

    companion object {
        const val TITLE_TYPE = 0
        const val WORD_TYPE = 1
    }
}