package ar.com.play2play.presentation.truco.cards

import android.view.View
import ar.com.play2play.R

class TrucoFor4MyCardsHand(
    cards: List<PlayingCard>,
    droppingPlaces: List<View>,
    listener: Listener?
) : TrucoCardsHand(cards, droppingPlaces, listener) {

    private val cardsHorizontalMargins = listOf(
        R.dimen.truco_for_4_my_first_card_horizontal_margin,
        R.dimen.truco_for_4_my_second_card_horizontal_margin,
        R.dimen.truco_for_4_my_third_card_horizontal_margin
    ).map { context.resources.getDimension(it) }
    private val normalVerticalSize = context.resources.getDimension(R.dimen.truco_normal_card_vertical_margin)
    private val elevatedVerticalSize = context.resources.getDimension(R.dimen.truco_for_4_elevated_card_vertical_margin)
    private val cardsRotationForHand = mapOf(
        COMPLETE_HAND to listOf(-15f, -5f, 5f),
        TWO_CARDS to listOf(-5f, 5f),
        SINGLE_CARD to listOf(0f),
    )

    override fun isAbleToDrag() = true

    override fun getCardsRotation(playingCards: Int) = cardsRotationForHand.getValue(playingCards)

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
