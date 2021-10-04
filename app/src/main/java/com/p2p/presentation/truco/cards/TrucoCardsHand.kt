package com.p2p.presentation.truco.cards

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.p2p.R
import com.p2p.model.truco.Card
import com.p2p.presentation.extensions.fadeIn
import com.p2p.presentation.truco.TrucoDragAndDropCard
import kotlin.math.abs

abstract class TrucoCardsHand(
    cards: List<PlayingCard>,
    private val droppingPlaces: List<View>,
    private val listener: Listener?
) : TrucoDragAndDropCard.Listener {

    private val dragAndDropCards = cards
        .map { TrucoDragAndDropCard(it.view, this) to it }
        .toMap()
    private val dragAndDropCardsInHand = dragAndDropCards.keys.toMutableList()

    protected val context: Context = cards.first().view.context
    private val trucoCardFinalRotation by lazy {
        context.resources.getInteger(R.integer.truco_card_final_rotation).toFloat()
    }

    init {
        cards.first().view.post { updateCardsHandUI() }
    }

    /** Taking the turn will enable the user to touch and move the card to the next available dropping place. */
    fun takeTurn() {
        droppingPlaces.firstOrNull { !it.isVisible }?.let { updateDroppingPlace(it) }
    }

    fun playCard(card: Card, droppingPlace: View, round: Int) {
        val dragAndDropCardEntry = dragAndDropCards.toList()[round]
        val cardView = dragAndDropCardEntry.second.view
        val movingAnimation = cardView.animate()
        movingAnimation
            .x(droppingPlace.x)
            .y(droppingPlace.y)
            .scaleX(droppingPlace.scaleX)
            .scaleY(droppingPlace.scaleY)
            .rotation(0f)
            .rotationX(droppingPlace.rotationX)
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator?) {
                    updateUIAfterHandChange(dragAndDropCardEntry.first, false)
                    cardView.elevation = COMPLETE_HAND - dragAndDropCardsInHand.size.toFloat()
                }

                override fun onAnimationEnd(animator: Animator?) {
                    movingAnimation.setListener(null)
                    val flipAnimation = cardView.animate()
                    flipAnimation
                        .rotationY(0f)
                        .setUpdateListener {
                            if (it.animatedFraction >= HALF_ANIMATION) {
                                flipAnimation.setUpdateListener(null)
                                CardImageCreator.loadCard(cardView, card)
                            }
                        }
                        .start()
                }
            })
            .start()
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
            listener?.onCardPlayed(dragAndDropCards.getValue(dragAndDropCard))
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
        val cardsRotation = getCardsRotation(playingCardsSize)
        dragAndDropCardsInHand.forEachIndexed { index, dragAndDropCard ->
            val cardView = dragAndDropCard.cardView
            cardView.elevation = if (shouldSetCardInitialElevation()) index.toFloat() else 0f
            cardView.animate()
                .x(getCardX(cardView, index))
                .y(getCardY(cardView, playingCardsSize, index))
                .rotation(cardsRotation[index])
                .start()
        }
    }

    protected abstract fun getCardsRotation(playingCards: Int): List<Float>

    protected abstract fun getCardX(cardView: View, cardIndex: Int): Float

    protected abstract fun getCardY(cardView: View, playingCards: Int, cardIndex: Int): Float

    protected abstract fun shouldSetCardInitialElevation(): Boolean

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

        fun onCardPlayed(playingCard: PlayingCard)
    }

    data class PlayingCard(val card: Card, val view: ImageView)

    companion object {

        const val COMPLETE_HAND = 3
        const val TWO_CARDS = 2
        const val SINGLE_CARD = 1
        const val FIRST_CARD = 0
        const val SECOND_CARD = 1
        private const val HALF_ANIMATION = 0.5
        private const val THREE_QUARTERS = 0.75
    }
}