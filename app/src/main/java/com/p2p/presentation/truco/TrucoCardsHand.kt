package com.p2p.presentation.truco

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.p2p.R
import com.p2p.model.truco.Card
import com.p2p.presentation.extensions.fadeIn
import kotlin.math.abs

class TrucoCardsHand(
    cards: List<PlayingCard>,
    private val droppingPlaces: List<View>,
    private val listener: Listener
) : TrucoDragAndDropCard.Listener {

    private val dragAndDropCards = cards
        .map { TrucoDragAndDropCard(it.view, this) to it }
        .toMap()
    private val dragAndDropCardsInHand = dragAndDropCards.keys.toMutableList()

    private val context: Context = droppingPlaces.first().context

    private val cardsHorizontalMargins = listOf(
        R.dimen.truco_first_card_horizontal_margin,
        R.dimen.truco_second_card_horizontal_margin,
        R.dimen.truco_third_card_horizontal_margin
    ).map { context.resources.getDimension(it) }
    private val normalVerticalSize = context.resources.getDimension(R.dimen.truco_normal_card_vertical_margin)
    private val elevatedVerticalSize = context.resources.getDimension(R.dimen.truco_elevated_card_vertical_margin)

    private val cardsRotationForHand = mapOf(
        COMPLETE_HAND to listOf(-25f, -5f, 15f),
        TWO_CARDS to listOf(-10f, 10f),
        SINGLE_CARD to listOf(0f),
    )

    init {
        droppingPlaces.first().post { updateCardsHandUI() }
    }

    fun takeTurn() {
        droppingPlaces.firstOrNull { !it.isVisible }?.let { updateDroppingPlace(it) }
    }

    override fun onTouch(dragAndDropCard: TrucoDragAndDropCard) {
        dragAndDropCard.cardView.elevation = COMPLETE_HAND + 1f
    }

    /**
     * The card was dropped.
     *
     * If it was dropped inside the indicated view then it's no longer in the hand.
     * Otherwise it returns to the hand.
     */
    override fun onDrop(dragAndDropCard: TrucoDragAndDropCard, isInDroppingView: Boolean) {
        updateUIAfterHandChange(dragAndDropCard, isInTheHand = !isInDroppingView)
        if (isInDroppingView) {
            dragAndDropCard.cardView.elevation = droppingPlaces.count { it.isVisible }.toFloat() - 1
            updateDroppingPlace(null)
            listener.onCardPlayed(dragAndDropCards.getValue(dragAndDropCard))
        }
    }

    /**
     * The card was moved.
     *
     * If it moves more than half out of the hand, then it is taken out of the hand
     * and it won't return to the hand until the card is dropped out of the dropping view.
     */
    override fun onMove(dragAndDropCard: TrucoDragAndDropCard) {
        val cardView = dragAndDropCard.cardView
        val wasInTheHand = dragAndDropCard in dragAndDropCardsInHand
        val isStillInTheHand = abs(cardView.y) <= cardView.height * THREE_QUARTERS
        updateUIAfterHandChange(dragAndDropCard, isInTheHand = wasInTheHand && isStillInTheHand)
    }

    private fun updateCardsHandUI() {
        val playingCardsSize = dragAndDropCardsInHand.size.takeIf { it > 0 } ?: return
        val cardsRotation = cardsRotationForHand.getValue(playingCardsSize)
        dragAndDropCardsInHand.forEachIndexed { index, dragAndDropCard ->
            val cardView = dragAndDropCard.cardView
            val cardHorizontalMargin = cardsHorizontalMargins[index]
            val cardVerticalMargin = if (playingCardsSize == COMPLETE_HAND && index == SECOND_CARD) {
                elevatedVerticalSize
            } else {
                normalVerticalSize
            }
            cardView.elevation = index.toFloat()
            cardView.animate()
                .x(cardHorizontalMargin)
                .y((cardView.parent as View).height - cardView.height - cardVerticalMargin)
                .rotation(cardsRotation[index])
                .start()
        }
    }

    private fun updateUIAfterHandChange(dragAndDropCard: TrucoDragAndDropCard, isInTheHand: Boolean) {
        val wasInTheHand = dragAndDropCard in dragAndDropCardsInHand
        if (isInTheHand != wasInTheHand) {
            if (isInTheHand) {
                dragAndDropCardsInHand.add(dragAndDropCard)
            } else {
                dragAndDropCardsInHand.remove(dragAndDropCard)
            }
            updateCardsHandUI()
        }

    }

    private fun updateDroppingPlace(droppingPlace: View?) {
        droppingPlace?.elevation = droppingPlaces.lastOrNull { it.isVisible }?.elevation?.plus(1) ?: 0f
        droppingPlace?.fadeIn()
        dragAndDropCards.keys.forEach { it.droppingView = droppingPlace }
    }

    interface Listener {

        fun onCardPlayed(card: PlayingCard)
    }

    data class PlayingCard(val card: Card, val view: View)

    companion object {

        private const val COMPLETE_HAND = 3
        private const val TWO_CARDS = 2
        private const val SINGLE_CARD = 1
        private const val SECOND_CARD = 1
        private const val THREE_QUARTERS = 0.75
    }
}