package com.p2p.presentation.truco.cards

import android.view.View
import com.p2p.R

abstract class TrucoSingleOpponentCardsHand(
    cards: List<PlayingCard>,
    droppingPlaces: List<View>,
    listener: Listener?
) : TrucoCardsHand(cards, droppingPlaces, listener) {

    protected val normalVerticalSize = context.resources.getDimension(R.dimen.truco_normal_card_vertical_margin)
    protected val elevatedVerticalSize = context.resources.getDimension(R.dimen.truco_elevated_card_vertical_margin)

    protected val cardsRotationForHand = mapOf(
        COMPLETE_HAND to listOf(-25f, -5f, 15f),
        TWO_CARDS to listOf(-10f, 10f),
        SINGLE_CARD to listOf(0f),
    )

    companion object {

        const val SECOND_CARD = 1
    }
}