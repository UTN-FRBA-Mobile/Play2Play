package ar.com.play2play.presentation.truco.cards

import android.view.View
import android.widget.ImageView
import ar.com.play2play.R
import ar.com.play2play.model.truco.Card

abstract class TrucoFor4OtherPlayerCardsHand(
    previousCardsHand: TrucoFor4OtherPlayerCardsHand?,
    cards: List<PlayingCard>
) : TrucoCardsHand(cards, emptyList(), null) {

    constructor(
        previousCardsHand: TrucoFor4OtherPlayerCardsHand?,
        vararg cardViews: ImageView
    ) : this(previousCardsHand, cardViews.map { PlayingCard(Card.unknown(), it) })

    private val previousInitialCardY = previousCardsHand?.initialCardY
    protected val initialCardY: Float by lazy { previousInitialCardY ?: cards.first().view.y }
    protected val cardsHorizontalMargins = listOf(
        R.dimen.truco_for_4_their_first_card_horizontal_margin,
        R.dimen.truco_for_4_their_second_card_horizontal_margin,
        R.dimen.truco_for_4_their_third_card_horizontal_margin,
    ).map { context.resources.getDimension(it) }

    override val initialRotationY: Float by lazy {
        cards.first().view.resources.getInteger(R.integer.flip_rotation).toFloat()
    }

    final override fun getCardY(cardView: View, playingCards: Int, cardIndex: Int) = initialCardY

    final override fun getCardsRotation(playingCards: Int) = listOf(0f, 0f, 0f)

    final override fun shouldSetCardInitialElevation() = false

    override fun getInitialScale() = 0.3f
}
