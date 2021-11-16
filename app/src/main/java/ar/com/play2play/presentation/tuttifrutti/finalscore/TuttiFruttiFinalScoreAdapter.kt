package ar.com.play2play.presentation.tuttifrutti.finalscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ar.com.play2play.R
import ar.com.play2play.databinding.ViewTuttiFruttiFinalScoreItemBinding
import ar.com.play2play.model.tuttifrutti.TuttiFruttiFinalScore
import ar.com.play2play.utils.isEven

class TuttiFruttiFinalScoreAdapter :
    RecyclerView.Adapter<TuttiFruttiFinalScoreAdapter.ViewHolder>() {

    var results = listOf<TuttiFruttiFinalScore>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackground else R.color.wild_sand

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TuttiFruttiFinalScoreAdapter.ViewHolder {
        return ViewHolder(
            ViewTuttiFruttiFinalScoreItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TuttiFruttiFinalScoreAdapter.ViewHolder, position: Int) {
        return holder.bind(results[position], position)
    }

    override fun getItemCount() = results.size

    inner class ViewHolder(private val binding: ViewTuttiFruttiFinalScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [final score] into the view. */
        fun bind(userFinalScore: TuttiFruttiFinalScore, position: Int) = with(binding) {
            finalPosition.text = (position + 1).toString()
            playerName.text = userFinalScore.player
            points.text = userFinalScore.finalScore.toString()
            bigCrown.isVisible = position == 0
            finalScoreItem.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getBackgroundColor(position)
                )
            )
        }
    }
}