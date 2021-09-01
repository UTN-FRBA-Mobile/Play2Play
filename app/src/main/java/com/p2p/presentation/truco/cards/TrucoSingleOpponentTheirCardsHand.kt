package com.p2p.presentation.truco.cards

import android.view.View
import com.p2p.R

class TrucoSingleOpponentTheirCardsHand(
    cards: List<PlayingCard>
) : TrucoSingleOpponentCardsHand(cards, emptyList(), null) {

    private val initialCardY: Float by lazy { cards.first().view.y }
    private val cardsHorizontalMargins = listOf(
        R.dimen.truco_their_first_card_horizontal_margin,
        R.dimen.truco_their_second_card_horizontal_margin,
        R.dimen.truco_their_third_card_horizontal_margin
    ).map { context.resources.getDimension(it) }

    override fun getCardsRotation(playingCards: Int) = cardsRotationForHand.getValue(playingCards)

    override fun getCardX(cardView: View, cardIndex: Int): Float {
        val margin = cardsHorizontalMargins[cardIndex]
        return (cardView.parent as View).width - cardView.width - margin
    }

    override fun getCardY(cardView: View, playingCards: Int, cardIndex: Int): Float {
        return initialCardY - if (playingCards == COMPLETE_HAND && cardIndex == SECOND_CARD) {
            elevatedVerticalSize
        } else {
            normalVerticalSize
        }
    }

    override fun getExtraCardElevation() = false
}