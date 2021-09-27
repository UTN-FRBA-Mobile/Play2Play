package com.p2p.presentation.truco.cards

import android.view.View
import android.widget.ImageView
import com.p2p.R
import com.p2p.model.truco.Card

abstract class TrucoFor4OtherPlayerCardsHand(
    cards: List<PlayingCard>
) : TrucoCardsHand(cards, emptyList(), null) {

    constructor(vararg cardViews: ImageView) : this(cardViews.map { PlayingCard(Card.unknown(), it) })

    protected abstract val cardsRotationForHand: Map<Int, List<Float>>

    protected val initialCardY: Float by lazy { cards.first().view.y }
    protected val cardHeight: Int by lazy { cards.first().view.height }
    protected val cardsHorizontalMargins = listOf(
        R.dimen.truco_for_4_their_first_card_horizontal_margin,
        R.dimen.truco_for_4_their_second_card_horizontal_margin,
        R.dimen.truco_for_4_their_third_card_horizontal_margin,
    ).map { context.resources.getDimension(it) }

    protected fun getExtraCardY(playingCards: Int, cardIndex: Int) = cardHeight * when (cardIndex) {
        FIRST_CARD -> 0.3f
        SECOND_CARD -> if (playingCards == COMPLETE_HAND) 0.15f else 0.2f
        else -> 0.1f
    }

    final override fun getCardsRotation(playingCards: Int) = cardsRotationForHand.getValue(playingCards)

    final override fun shouldSetCardInitialElevation() = false
}
