package com.p2p.presentation.truco.cards

import android.widget.ImageView
import com.p2p.R
import com.p2p.model.truco.Card

abstract class TrucoFor4OtherPlayerCardsHand(
    previousCardsHand: TrucoFor4OtherPlayerCardsHand?,
    cards: List<PlayingCard>
) : TrucoCardsHand(cards, emptyList(), null) {

    constructor(
        previousCardsHand: TrucoFor4OtherPlayerCardsHand?,
        vararg cardViews: ImageView
    ) : this(previousCardsHand, cardViews.map { PlayingCard(Card.unknown(), it) })

    protected abstract val cardsRotationForHand: Map<Int, List<Float>>

    private val previousInitialCardY = previousCardsHand?.initialCardY
    protected val initialCardY: Float by lazy { previousInitialCardY ?: cards.first().view.y }
    private val cardHeight: Int by lazy { cards.first().view.height }
    protected val cardsHorizontalMargins = listOf(
        R.dimen.truco_for_4_their_first_card_horizontal_margin,
        R.dimen.truco_for_4_their_second_card_horizontal_margin,
        R.dimen.truco_for_4_their_third_card_horizontal_margin,
    ).map { context.resources.getDimension(it) }

    override val initialRotationY: Float by lazy {
        cards.first().view.resources.getInteger(R.integer.flip_rotation).toFloat()
    }

    protected fun getExtraCardY(playingCards: Int, cardIndex: Int) = cardHeight * when (cardIndex) {
        FIRST_CARD -> 0.3f
        SECOND_CARD -> if (playingCards == COMPLETE_HAND) 0.15f else 0.2f
        else -> 0.1f
    }

    final override fun getCardsRotation(playingCards: Int) = cardsRotationForHand.getValue(playingCards)

    final override fun shouldSetCardInitialElevation() = false
}
