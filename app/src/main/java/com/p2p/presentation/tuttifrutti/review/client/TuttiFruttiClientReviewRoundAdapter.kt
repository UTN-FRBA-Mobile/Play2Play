package com.p2p.presentation.tuttifrutti.review.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.p2p.R
import com.p2p.databinding.ViewClientReviewCategoryItemBinding
import com.p2p.databinding.ViewClientReviewWordItemBinding
import com.p2p.databinding.ViewReviewCategoryItemBinding
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.utils.isEven

/** The adapter used to show the list of round reviews for the client. */
class TuttiFruttiClientReviewRoundAdapter :
    RecyclerView.Adapter<TuttiFruttiClientReviewRoundAdapter.RecyclerViewHolder>() {

    var finishedRoundInfo = listOf<FinishedRoundInfo>()

    override fun getItemViewType(position: Int): Int {
        // If the the modulus number of players plus one is zero, then we get the category title view
        return if (position % (finishedRoundInfo.count() + 1) == 0) TITLE_TYPE else WORD_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        if (viewType == TITLE_TYPE) {
            return ClientReviewCategoryViewHolder(
                ViewClientReviewCategoryItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ClientReviewWordViewHolder(
            ViewClientReviewWordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

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
            RecyclerViewHolderParameters(
                player = wordViewPlayer,
                word = finishedRoundInfo[wordViewIndex].categoriesWords.getValue(viewCategory)
            )
        }

        return holder.bind(viewHolderParams, position)
    }

    // Return the number of players multiplied by the number of categories plus the number of categories (for the titles)
    override fun getItemCount() = finishedRoundInfo.count() * numberOfCategories() + numberOfCategories()

    private fun numberOfCategories() = finishedRoundInfo.first().categoriesWords.count()

    private fun getCategory(categoryIndex: Int): Category {
        return finishedRoundInfo.first().categoriesWords.toList()[categoryIndex].first
    }

    private fun getCategoryIndex(actualPosition: Int) = actualPosition / (finishedRoundInfo.count() + 1)

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.wild_sand else R.color.colorBackground

    inner class ClientReviewCategoryViewHolder(private val binding: ViewClientReviewCategoryItemBinding) : RecyclerViewHolder(binding) {
        /** Show the given [category] into the view. */
        override fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int) = with(binding) {
            categoryTitle.text = viewHolderParams.category
        }
    }

    inner class ClientReviewWordViewHolder(private val binding: ViewClientReviewWordItemBinding) : RecyclerViewHolder(binding) {
        /** Show the given [review] into the view. */
        override fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int) = with(binding) {
            val playerPosition = finishedRoundInfo.indexOfFirst { it.player == viewHolderParams.player  }
            playerName.text = viewHolderParams.player
            playerWord.text = viewHolderParams.word
            clientReviewWordContainer.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(playerPosition)
                )
            )
        }
    }

    abstract class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(viewHolderParams : RecyclerViewHolderParameters, position: Int)
    }

    companion object {
        const val TITLE_TYPE = 0
        const val WORD_TYPE = 1
    }
}
