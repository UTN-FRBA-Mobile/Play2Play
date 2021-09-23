package com.p2p.presentation.truco.cards

import android.view.View
import com.p2p.R

class TrucoFor4FrontPlayerCardsHand(
    cards: List<PlayingCard>
) : TrucoCardsHand(cards, emptyList(), null) {

    private val initialCardY: Float by lazy { cards.first().view.y }
    private val cardHeight: Int by lazy { cards.first().view.height }
    private val cardsHorizontalMargins = listOf(
        R.dimen.truco_for_4_their_first_card_horizontal_margin,
        R.dimen.truco_for_4_their_second_card_horizontal_margin,
        R.dimen.truco_for_4_their_third_card_horizontal_margin,
    ).map { context.resources.getDimension(it) }
    private val cardsRotationForHand = mapOf(
        COMPLETE_HAND to listOf(-50f, -30f, -10f),
        TWO_CARDS to listOf(-40f, -20f),
        SINGLE_CARD to listOf(-30f),
    )

    override fun getCardsRotation(playingCards: Int) = cardsRotationForHand.getValue(playingCards)

    override fun getCardX(cardView: View, cardIndex: Int): Float {
        val margin = cardsHorizontalMargins[cardIndex]
        return (cardView.parent as View).width - cardView.width - margin
    }

    override fun getCardY(cardView: View, playingCards: Int, cardIndex: Int): Float {
        return initialCardY + cardHeight * when (cardIndex) {
            FIRST_CARD -> 0.3f
            SECOND_CARD -> if (playingCards == COMPLETE_HAND) 0.15f else 0.2f
            else -> 0.1f
        }
    }

    override fun shouldSetCardInitialElevation() = false
}
