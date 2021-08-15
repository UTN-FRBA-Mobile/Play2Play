package com.p2p.presentation.home.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewGamesItemBinding
import com.p2p.utils.isEven

/** The adapter used to show the list of games. */
class GamesAdapter(private val onSelectedChanged: (Game?) -> Unit) :
    RecyclerView.Adapter<GamesAdapter.ViewHolder>() {

    /** The list of games displayed on the recycler. */
    var games = listOf<Game>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** The current selected game on the list. */
    var selected: Game? = null
        private set(value) {
            if (field == value) return
            field = value
            onSelectedChanged.invoke(value)
            notifyDataSetChanged()
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

    override fun getItemCount() = games.size

    private fun getGameCardBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorSecondaryVariant else R.color.colorPrimaryVariant

    private fun getNameBackgroundColor(index: Int) =
        if (index.isEven()) R.color.colorSecondary else R.color.colorPrimary

    inner class ViewHolder(private val binding: ViewGamesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [game] into the view. */
        fun bind(game: Game) = with(binding) {
            gameCard.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getGameCardBackgroundColor(position)
                )
            )
            name.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    getNameBackgroundColor(position)
                )
            )
            gameCardIcon.setBackgroundResource(game.iconRes)
            name.text = name.context.getText(game.nameRes)
            container.setOnClickListener { selected = game }
            container
                .animate()
                .alpha(
                    when (selected) {
                        game -> SELECTED_OPACITY
                        null -> NONE_SELECTED_OPACITY
                        else -> NO_SELECTED_OPACITY
                    }
                )
                .start()
        }
    }

    companion object {

        private const val SELECTED_OPACITY = 1f
        private const val NO_SELECTED_OPACITY = 0.5f
        private const val NONE_SELECTED_OPACITY = 0.8f
    }
}
