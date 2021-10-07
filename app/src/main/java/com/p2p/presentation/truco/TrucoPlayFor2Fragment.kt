package com.p2p.presentation.truco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.p2p.R
import com.p2p.databinding.FragmentPlayTrucoFor2Binding
import com.p2p.model.truco.Card
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.presentation.truco.cards.TrucoSingleOpponentMyCardsHand
import com.p2p.presentation.truco.cards.TrucoSingleOpponentTheirCardsHand

class TrucoPlayFor2Fragment : TrucoFragment<FragmentPlayTrucoFor2Binding>() {

    override val gameViewModel by activityViewModels<TrucoViewModel>()
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTrucoFor2Binding =
        FragmentPlayTrucoFor2Binding::inflate

    private lateinit var theirCardsHand: TrucoSingleOpponentTheirCardsHand
    private lateinit var theirCardsViews: List<ImageView>
    private lateinit var theirDroppingPlacesViews: List<View>

    override fun initUI() = with(gameBinding) {
        theirCardsViews = listOf(theirLeftCard, theirMiddleCard, theirRightCard)
        theirDroppingPlacesViews = listOf(dropTheirFirstCard, dropTheirSecondCard, dropTheirThirdCard)
        super.initUI()
    }

    override fun initializeRivalHands(isFirstHand: Boolean) = with(gameBinding) {
        theirCardsHand = TrucoSingleOpponentTheirCardsHand(
            previousCardsHand = if (isFirstHand) null else theirCardsHand,
            cards = theirCardsViews.map { TrucoCardsHand.PlayingCard(Card.unknown(), it) }
        )
        loadCardImages(theirCardsViews, emptyList())
    }

    override fun hideAllActions() {
        hideActionBubble(gameBinding.theirActionBubble, gameBinding.theirActionBubbleText)
    }

    override fun getPlayerCardsHand(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.MY_SELF -> myCardsHand
        TrucoPlayerPosition.FRONT -> theirCardsHand
        else -> throw IllegalStateException("There's only myself and front player on truco for 2")
    }

    override fun createMyCardsHand(myPlayingCards: List<TrucoCardsHand.PlayingCard>): TrucoCardsHand {
        return TrucoSingleOpponentMyCardsHand(myPlayingCards, myDroppingPlacesViews, this)
    }

    override fun getDroppingPlaces(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.MY_SELF -> myDroppingPlacesViews
        TrucoPlayerPosition.FRONT -> theirDroppingPlacesViews
        else -> throw IllegalStateException("There's only myself and front player on truco for 2")
    }

    override fun getPlayerBubbleWithTextView(playerPosition: TrucoPlayerPosition): Pair<View, TextView> =
        when (playerPosition) {
            TrucoPlayerPosition.MY_SELF -> myActionBubble
            TrucoPlayerPosition.FRONT -> {
                val (bubble, text) = bubbleForPosition(TrucoPlayerPosition.FRONT)
                requireView().findViewById<View>(bubble) to requireView().findViewById(text)
            }
            else -> throw IllegalStateException("There's only myself and front player on truco for 2")
        }

    override fun bubbleForPosition(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.MY_SELF -> R.id.my_action_bubble to R.id.my_action_bubble_text
        TrucoPlayerPosition.FRONT -> R.id.their_action_bubble to R.id.their_action_bubble_text
        else -> throw IllegalStateException("There's only myself and front player on truco for 2")
    }

    override fun clearPlayedCards() {
        loadCardImages(theirCardsViews, emptyList())
    }

    companion object {

        fun newInstance() = TrucoPlayFor2Fragment()
    }

}