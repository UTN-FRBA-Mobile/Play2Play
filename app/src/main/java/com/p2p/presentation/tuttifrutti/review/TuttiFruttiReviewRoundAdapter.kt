package com.p2p.presentation.tuttifrutti.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.p2p.databinding.ViewReviewCategoryItemBinding
import com.p2p.databinding.ViewReviewWordItemBinding
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo

/** The adapter used to show the list of round reviews. */
class TuttiFruttiReviewRoundAdapter(
    private val onAddRoundPoints: (String, Int) -> Unit,
    private val onSubstractRoundPoints: (String, Int) -> Unit) :
    TuttiFruttiReviewRoundAdapterHelper() {

    var finishedRoundPointsInfo = listOf<FinishedRoundPointsInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        if (viewType == TITLE_TYPE) {
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

    override fun wordViewHolderParameters(wordViewPlayer: String, wordViewWord: String, categoryIndex: Int)
        = RecyclerViewHolderParameters(
            player = wordViewPlayer,
            word = wordViewWord,
            points = finishedRoundPointsInfo.first { it.player == wordViewPlayer }.wordsPoints[categoryIndex]
        )

    inner class ReviewCategoryViewHolder(private val binding: ViewReviewCategoryItemBinding) : RecyclerViewHolder(binding) {
        /** Show the given [category] into the view. */
        override fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int) = with(binding) {
            categoryTitle.text = viewHolderParams.category
        }
    }

    inner class ReviewWordViewHolder(private val binding: ViewReviewWordItemBinding) : RecyclerViewHolder(binding) {
        /** Show the given [review] into the view. */
        override fun bind(viewHolderParams: RecyclerViewHolderParameters, position: Int) = with(binding) {
            val playerPosition = finishedRoundInfo.indexOfFirst { it.player == viewHolderParams.player  }
            playerName.text = viewHolderParams.player
            playerWord.text = viewHolderParams.word
            playerPoints.text = viewHolderParams.points.toString()
            reviewWordContainer.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(playerPosition)
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

    companion object {
        const val MIN_SCORE = 0
        const val MIDDLE_SCORE = 5
        const val MAX_SCORE = 10
    }
}
