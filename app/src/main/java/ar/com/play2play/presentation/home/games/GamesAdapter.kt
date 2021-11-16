package ar.com.play2play.presentation.home.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ar.com.play2play.R
import ar.com.play2play.databinding.ViewGamesItemBinding
import ar.com.play2play.presentation.extensions.fadeIn
import ar.com.play2play.presentation.extensions.fadeOut
import ar.com.play2play.utils.isEven

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
                if (buttons.isVisible) return@setOnClickListener
                buttons.fadeIn()
                buttons.postDelayed({ buttons.fadeOut() }, VISIBLE_BUTTONS_TIME)
            }
            createButton.setOnClickListener { onCreateClicked(game) }
            joinButton.setOnClickListener { onJoinClicked(game) }
        }
    }

    private class Differ : DiffUtil.ItemCallback<Game>() {

        override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
    }

    companion object {

        private const val VISIBLE_BUTTONS_TIME = 10_000L
    }
}
