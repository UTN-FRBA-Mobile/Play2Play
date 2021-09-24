package com.p2p.presentation.truco.finalscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewTrucoFinalScoreItemBinding
import com.p2p.utils.isEven

class TrucoFinalScoreAdapter :
    RecyclerView.Adapter<TrucoFinalScoreAdapter.ViewHolder>() {

    var results = listOf<TrucoFinalScore>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private fun getBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorBackground else R.color.wild_sand

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrucoFinalScoreAdapter.ViewHolder {
        return ViewHolder(
            ViewTrucoFinalScoreItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrucoFinalScoreAdapter.ViewHolder, position: Int) {
        return holder.bind(results[position], position)
    }

    override fun getItemCount() = results.size

    inner class ViewHolder(private val binding: ViewTrucoFinalScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [final score] into the view. */
        fun bind(userFinalScore: TrucoFinalScore, position: Int) = with(binding) {
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