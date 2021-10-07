package com.p2p.presentation.truco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.p2p.R
import com.p2p.databinding.FragmentPlayTrucoFor4Binding
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4FrontPlayerCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4LeftPlayerCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4MyCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4RightPlayerCardsHand

class TrucoPlayFor4Fragment : TrucoFragment<FragmentPlayTrucoFor4Binding>() {

    override val gameViewModel by activityViewModels<TrucoViewModel>()
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTrucoFor4Binding =
        FragmentPlayTrucoFor4Binding::inflate

    private lateinit var frontPlayerCardsHand: TrucoCardsHand
    private lateinit var leftPlayerCardsHand: TrucoCardsHand
    private lateinit var rightPlayerCardsHand: TrucoCardsHand

    private lateinit var frontPlayerCardViews: List<ImageView>
    private lateinit var frontPlayerDroppingPlacesViews: List<View>
    private lateinit var leftPlayerCardViews: List<ImageView>
    private lateinit var leftPlayerDroppingPlacesViews: List<View>
    private lateinit var rightPlayerCardViews: List<ImageView>
    private lateinit var rightPlayerDroppingPlacesViews: List<View>

    override fun initUI() = with(gameBinding) {
        super.initUI()
        frontPlayerCardViews = listOf(frontPlayerLeftCard, frontPlayerMiddleCard, frontPlayerRightCard)
        frontPlayerDroppingPlacesViews =
            listOf(dropFrontPlayerFirstCard, dropFrontPlayerSecondCard, dropFrontPlayerThirdCard)
        frontPlayerCardsHand = TrucoFor4FrontPlayerCardsHand(frontPlayerCardViews)
        loadCardImages(frontPlayerCardViews, emptyList())
        leftPlayerCardViews = listOf(leftPlayerLeftCard, leftPlayerMiddleCard, leftPlayerRightCard)
        leftPlayerDroppingPlacesViews =
            listOf(dropLeftPlayerFirstCard, dropLeftPlayerSecondCard, dropLeftPlayerThirdCard)
        leftPlayerCardsHand = TrucoFor4LeftPlayerCardsHand(leftPlayerCardViews)
        loadCardImages(leftPlayerCardViews, emptyList())
        rightPlayerCardViews = listOf(rightPlayerLeftCard, rightPlayerMiddleCard, rightPlayerRightCard)
        rightPlayerDroppingPlacesViews =
            listOf(dropRightPlayerFirstCard, dropRightPlayerSecondCard, dropRightPlayerThirdCard)
        rightPlayerCardsHand = TrucoFor4RightPlayerCardsHand(rightPlayerCardViews)
        loadCardImages(rightPlayerCardViews, emptyList())
    }

    override fun hideAllActions() {
        hideActionBubble(gameBinding.leftPlayerActionBubble, gameBinding.leftPlayerActionBubbleText)
        hideActionBubble(gameBinding.frontPlayerActionBubble, gameBinding.frontPlayerActionBubbleText)
        hideActionBubble(gameBinding.rightPlayerActionBubble, gameBinding.rightPlayerActionBubbleText)
    }

    override fun createMyCardsHand(myPlayingCards: List<TrucoCardsHand.PlayingCard>): TrucoCardsHand {
        return TrucoFor4MyCardsHand(myPlayingCards, myDroppingPlacesViews, this)
    }

    override fun getPlayerCardsHand(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.FRONT -> frontPlayerCardsHand
        TrucoPlayerPosition.LEFT -> leftPlayerCardsHand
        TrucoPlayerPosition.RIGHT -> rightPlayerCardsHand
        TrucoPlayerPosition.MY_SELF -> myCardsHand
    }

    override fun getDroppingPlaces(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.FRONT -> frontPlayerDroppingPlacesViews
        TrucoPlayerPosition.LEFT -> leftPlayerDroppingPlacesViews
        TrucoPlayerPosition.RIGHT -> rightPlayerDroppingPlacesViews
        TrucoPlayerPosition.MY_SELF -> myDroppingPlacesViews
    }

    override fun getPlayerBubbleWithTextView(playerPosition: TrucoPlayerPosition): Pair<View, TextView>{
        val (bubble, text) = bubbleForPosition(playerPosition)
        return requireView().findViewById<View>(bubble) to requireView().findViewById<TextView>(text)
    }

    override fun bubbleForPosition(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.FRONT -> R.id.front_player_action_bubble to R.id.front_player_action_bubble_text
        TrucoPlayerPosition.LEFT -> R.id.left_player_action_bubble to R.id.left_player_action_bubble_text
        TrucoPlayerPosition.RIGHT -> R.id.right_player_action_bubble to R.id.right_player_action_bubble_text
        TrucoPlayerPosition.MY_SELF -> R.id.my_action_bubble to R.id.my_action_bubble_text
    }

    override fun clearPlayedCards() {
        loadCardImages(rightPlayerCardViews, emptyList())
        loadCardImages(leftPlayerCardViews, emptyList())
        loadCardImages(frontPlayerCardViews, emptyList())
    }


    companion object {
        fun newInstance() = TrucoPlayFor4Fragment()
    }
}