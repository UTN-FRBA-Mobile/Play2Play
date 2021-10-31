package com.p2p.presentation.home.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewGamesItemBinding
import com.p2p.presentation.extensions.fadeIn
import com.p2p.presentation.extensions.fadeOut
import com.p2p.utils.isEven

/** The adapter used to show the list of games. */
class GamesAdapter(
    private val onCreateClicked: (Game) -> Unit,
    private val onJoinClicked: (Game) -> Unit
) : ListAdapter<Game, GamesAdapter.ViewHolder>(Differ()) {

    /** The list of games displayed on the recycler. */
    var games = listOf<Game>()
        set(value) {
            submitList(value)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewGamesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(games[position])

    private fun getGameCardBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorSecondaryVariant else R.color.colorPrimaryVariant

    private fun getNameBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorSecondary else R.color.colorPrimary

    inner class ViewHolder(private val binding: ViewGamesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [game] into the view. */
        fun bind(game: Game) = with(binding) {
            buttons.isVisible = false
            gameCard.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getGameCardBackgroundColor(absoluteAdapterPosition)
                )
            )
            name.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getNameBackgroundColor(absoluteAdapterPosition)
                )
            )
            gameCardIcon.setBackgroundResource(game.iconRes)
            name.text = name.context.getText(game.nameRes)
            container.setOnClickListener {
                buttons.fadeIn()
                buttons.postDelayed({ buttons.fadeOut() }, 10_000L)
            }
            createButton.setOnClickListener { onCreateClicked(game) }
            joinButton.setOnClickListener { onJoinClicked(game) }
        }
    }

    class Differ : DiffUtil.ItemCallback<Game>() {

        override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
    }
}
