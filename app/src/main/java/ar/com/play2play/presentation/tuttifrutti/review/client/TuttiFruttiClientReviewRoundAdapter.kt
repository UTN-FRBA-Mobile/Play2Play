package ar.com.play2play.presentation.tuttifrutti.review.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import ar.com.play2play.R
import ar.com.play2play.databinding.ViewClientReviewCategoryItemBinding
import ar.com.play2play.databinding.ViewClientReviewWordItemBinding
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category
import ar.com.play2play.presentation.tuttifrutti.review.RecyclerViewHolderParameters
import ar.com.play2play.presentation.tuttifrutti.review.TuttiFruttiReviewRoundAdapterHelper
import ar.com.play2play.utils.isEven

/** The adapter used to show the list of round reviews for the client. */
class TuttiFruttiClientReviewRoundAdapter :
    TuttiFruttiReviewRoundAdapterHelper() {

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

    override fun wordViewHolderParameters(wordViewPlayer: String, wordViewWord: String, categoryIndex: Int)
        = RecyclerViewHolderParameters(player = wordViewPlayer, word = wordViewWord)

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
}
