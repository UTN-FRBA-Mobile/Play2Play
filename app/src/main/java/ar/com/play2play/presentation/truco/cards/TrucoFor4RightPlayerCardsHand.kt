package ar.com.play2play.presentation.truco.cards

import android.view.View
import android.widget.ImageView

class TrucoFor4RightPlayerCardsHand(
    previousCardsHand: TrucoFor4OtherPlayerCardsHand?,
    cardViews: List<ImageView>
) : TrucoFor4OtherPlayerCardsHand(previousCardsHand, *cardViews.toTypedArray()) {

    override fun getCardX(cardView: View, cardIndex: Int): Float {
        val margin = cardsHorizontalMargins[cardIndex]
        return (cardView.parent as View).width - cardView.width - margin
    }

    override fun getPivotX() = 1f

    override fun getPivotY() = 0f
}
