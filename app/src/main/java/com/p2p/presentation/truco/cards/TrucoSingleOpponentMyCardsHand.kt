package com.p2p.presentation.truco.cards

import android.view.View
import com.p2p.R

class TrucoSingleOpponentMyCardsHand(
    cards: List<PlayingCard>,
    droppingPlaces: List<View>,
    listener: Listener?
) : TrucoSingleOpponentCardsHand(cards, droppingPlaces, listener) {

    private val cardsHorizontalMargins = listOf(
        R.dimen.truco_first_card_horizontal_margin,
        R.dimen.truco_second_card_horizontal_margin,
        R.dimen.truco_third_card_horizontal_margin
    ).map { context.resources.getDimension(it) }

    override fun getCardX(cardView: View, cardIndex: Int) = cardsHorizontalMargins[cardIndex]

    override fun getCardY(cardView: View, playingCards: Int, cardIndex: Int): Float {
        val margin = if (playingCards == COMPLETE_HAND && cardIndex == SECOND_CARD) {
            elevatedVerticalSize
        } else {
            normalVerticalSize
        }
        return (cardView.parent as View).height - cardView.height - margin
    }

    override fun shouldSetCardInitialElevation() = true
}
