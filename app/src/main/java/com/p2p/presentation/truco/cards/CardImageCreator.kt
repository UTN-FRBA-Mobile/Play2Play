package com.p2p.presentation.truco.cards

import android.widget.ImageView
import com.p2p.R
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import java.util.Locale


object CardImageCreator {

    private const val CARD_UNKNOWN_RES = "card_unknown"
    private const val CARD_RES_NAME_FORMAT = "card_%d_%s"
    private const val DRAWABLE_RES_TYPE = "drawable"

    /**
     * Loads the image and the content description of a [card] on the given [view].
     *
     * If the card is null, then it set the back of the card.
     */
    fun loadCard(view: ImageView, card: Card?) {
        val resName = card
            ?.run { CARD_RES_NAME_FORMAT.format(number, suit.name.toLowerCase(Locale.US)) }
            ?: CARD_UNKNOWN_RES
        val identifier = view
            .resources
            .getIdentifier(resName, DRAWABLE_RES_TYPE, view.context.packageName)
        view.setImageResource(identifier)
        view.contentDescription = card
            ?.let { view.context.getString(getStringForSuit(it.suit), it.number) }
            ?: view.context.getString(R.string.truco_unknown_card)
    }

    private fun getStringForSuit(suit: Suit) = when (suit) {
        Suit.SWORDS -> R.string.truco_swords_card
        Suit.CLUBS -> R.string.truco_clubs_card
        Suit.GOLDS -> R.string.truco_golds_card
        Suit.CUPS -> R.string.truco_cups_card
    }
}
