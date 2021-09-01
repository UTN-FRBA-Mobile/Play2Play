package com.p2p.presentation.truco.cards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.p2p.R
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit

class CardImageCreator(val context: Context) {

    private val cardsDeck = BitmapFactory.decodeResource(context.resources, R.drawable.cards_deck)
    private val cardWidth = cardsDeck.width / HORIZONTAL_CARDS_COUNT
    private val cardHeight = cardsDeck.height / VERTICAL_CARDS_COUNT

    /**
     * Creates an image represented on a [Bitmap] and it content description on a [String] for the given [card].
     *
     * If the card is null, then it return the back of the card.
     */
    fun create(card: Card?): Pair<Bitmap, String> {
        val cardX = (card?.number?.minus(1) ?: NO_CARD_HORIZONTAL_POSITION) * cardWidth
        val cardY = (card?.let { getSuitVerticalPosition(it.suit) } ?: NO_CARD_VERTICAL_POSITION) * cardHeight
        val image = Bitmap.createBitmap(cardsDeck, cardX, cardY, cardWidth, cardHeight, null, false)
        val description = card
            ?.let { context.getString(getStringForSuit(it.suit), it.number) }
            ?: context.getString(R.string.truco_unknown_card)
        return image to description
    }

    private fun getSuitVerticalPosition(suit: Suit) = when (suit) {
        Suit.SWORDS -> SWORDS_VERTICAL_POSITION
        Suit.CLUBS -> CLUBS_VERTICAL_POSITION
        Suit.GOLDS -> GOLDS_VERTICAL_POSITION
        Suit.CUPS -> CUPS_VERTICAL_POSITION
    }

    private fun getStringForSuit(suit: Suit) = when (suit) {
        Suit.SWORDS -> R.string.truco_swords_card
        Suit.CLUBS -> R.string.truco_clubs_card
        Suit.GOLDS -> R.string.truco_golds_card
        Suit.CUPS -> R.string.truco_cups_card
    }

    companion object {

        private const val HORIZONTAL_CARDS_COUNT = 12
        private const val VERTICAL_CARDS_COUNT = 5
        private const val GOLDS_VERTICAL_POSITION = 0
        private const val CUPS_VERTICAL_POSITION = 1
        private const val SWORDS_VERTICAL_POSITION = 2
        private const val CLUBS_VERTICAL_POSITION = 3
        private const val NO_CARD_HORIZONTAL_POSITION = 1
        private const val NO_CARD_VERTICAL_POSITION = 4
    }
}
