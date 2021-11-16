package ar.com.play2play.presentation.truco.cards

import android.view.View
import android.widget.ImageView

class TrucoFor4LeftPlayerCardsHand(
    previousCardsHand: TrucoFor4OtherPlayerCardsHand?,
    cardViews: List<ImageView>
) : TrucoFor4OtherPlayerCardsHand(previousCardsHand, *cardViews.toTypedArray()) {

    override fun getCardX(cardView: View, cardIndex: Int) = cardsHorizontalMargins[cardIndex]

    override fun getPivotX() = 0f

    override fun getPivotY() = 1f
}