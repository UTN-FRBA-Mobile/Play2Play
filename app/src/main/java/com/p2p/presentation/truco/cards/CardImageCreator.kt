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

    /** Creates an image represented on a [Bitmap] for the given [card]. */
    fun create(card: Card): Bitmap {
        val cardX = (card.number - 1) * cardWidth
        val cardY = getSuitVerticalPosition(card.suit) * cardHeight
        return Bitmap.createBitmap(cardsDeck, cardX, cardY, cardWidth, cardHeight, null, false)
    }

    private fun getSuitVerticalPosition(suit: Suit) = when (suit) {
        Suit.SWORDS -> SWORDS_VERTICAL_POSITION
        Suit.CLUBS -> CLUBS_VERTICAL_POSITION
        Suit.GOLDS -> GOLDS_VERTICAL_POSITION
        Suit.CUPS -> CUPS_VERTICAL_POSITION
    }

    companion object {

        private const val HORIZONTAL_CARDS_COUNT = 12
        private const val VERTICAL_CARDS_COUNT = 5
        private const val GOLDS_VERTICAL_POSITION = 0
        private const val CUPS_VERTICAL_POSITION = 1
        private const val SWORDS_VERTICAL_POSITION = 2
        private const val CLUBS_VERTICAL_POSITION = 3
    }
}
