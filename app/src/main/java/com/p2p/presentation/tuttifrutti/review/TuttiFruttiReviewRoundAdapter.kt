package com.p2p.presentation.tuttifrutti.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.p2p.R
import com.p2p.databinding.ViewReviewCategoryItemBinding
import com.p2p.databinding.ViewReviewWordItemBinding
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.utils.isEven

/** The adapter used to show the list of round reviews. */
class TuttiFruttiReviewRoundAdapter(
    private val onAddRoundPoints: (String, Int) -> Unit,
    private val onSubstractRoundPoints: (String, Int) -> Unit) :
    RecyclerView.Adapter<TuttiFruttiReviewRoundAdapter.RecyclerViewHolder>() {

    var finishedRoundInfo = listOf<FinishedRoundInfo>()
    var finishedRoundPointsInfo = listOf<FinishedRoundPointsInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        // If the the modulus number of players plus one is zero, then we get the category title view
        return if (position % (finishedRoundInfo.count() + 1) == 0) TITLE_TYPE else WORD_TYPE
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
                word = finishedRoundInfo[wordViewIndex].categoriesWords.getValue(viewCategory),
                points = finishedRoundPointsInfo.first { it.player == wordViewPlayer }.wordsPoints[categoryIndex]
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
            playerPoints.text = viewHolderParams.points.toString()
            reviewWordContainer.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(position)
                )
            )

            when (viewHolderParams.points) {
                MIN_SCORE -> {
                    buttonAdd.isEnabled = true
                    buttonSubstract.isEnabled = false
                }
                MIDDLE_SCORE -> {
                    buttonAdd.isEnabled = true
                    buttonSubstract.isEnabled = true
                }
                MAX_SCORE -> {
                    buttonAdd.isEnabled = false
                    buttonSubstract.isEnabled = true
                }
            }

            buttonAdd.setOnClickListener {
                onAddRoundPoints.invoke(viewHolderParams.player, getCategoryIndex(position))
            }
            buttonSubstract.setOnClickListener {
                onSubstractRoundPoints.invoke(viewHolderParams.player, getCategoryIndex(position))
            }
        }
    }

    abstract class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(viewHolderParams : RecyclerViewHolderParameters, position: Int)
    }

    companion object {
        const val TITLE_TYPE = 0
        const val WORD_TYPE = 1
        const val MIN_SCORE = 0
        const val MIDDLE_SCORE = 5
        const val MAX_SCORE = 10
    }
}
