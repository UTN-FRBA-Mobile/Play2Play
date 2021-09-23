package com.p2p.presentation.truco.cards

import android.view.View

class TrucoFor4LeftPlayerCardsHand(cards: List<PlayingCard>) : TrucoFor4OtherPlayerCardsHand(cards) {

    override val cardsRotationForHand = mapOf(
        COMPLETE_HAND to listOf(50f, 30f, 10f),
        TWO_CARDS to listOf(40f, 20f),
        SINGLE_CARD to listOf(30f),
    )

    override fun getCardX(cardView: View, cardIndex: Int) = cardsHorizontalMargins[cardIndex]

    override fun getCardY(cardView: View, playingCards: Int, cardIndex: Int): Float {
        return initialCardY + getExtraCardY(playingCards, cardIndex)
    }
}