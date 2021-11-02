package com.p2p.presentation.truco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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

    private lateinit var frontPlayerCardsHand: TrucoFor4FrontPlayerCardsHand
    private lateinit var leftPlayerCardsHand: TrucoFor4LeftPlayerCardsHand
    private lateinit var rightPlayerCardsHand: TrucoFor4RightPlayerCardsHand

    private lateinit var frontPlayerCardViews: List<ImageView>
    private lateinit var frontPlayerDroppingPlacesViews: List<View>
    private lateinit var leftPlayerCardViews: List<ImageView>
    private lateinit var leftPlayerDroppingPlacesViews: List<View>
    private lateinit var rightPlayerCardViews: List<ImageView>
    private lateinit var rightPlayerDroppingPlacesViews: List<View>

    override fun initUI() = with(gameBinding) {
        frontPlayerCardViews = listOf(frontPlayerLeftCard, frontPlayerMiddleCard, frontPlayerRightCard)
        frontPlayerDroppingPlacesViews =
            listOf(dropFrontPlayerFirstCard, dropFrontPlayerSecondCard, dropFrontPlayerThirdCard)
        leftPlayerCardViews = listOf(leftPlayerLeftCard, leftPlayerMiddleCard, leftPlayerRightCard)
        leftPlayerDroppingPlacesViews =
            listOf(dropLeftPlayerFirstCard, dropLeftPlayerSecondCard, dropLeftPlayerThirdCard)
        rightPlayerCardViews = listOf(rightPlayerLeftCard, rightPlayerMiddleCard, rightPlayerRightCard)
        rightPlayerDroppingPlacesViews =
            listOf(dropRightPlayerFirstCard, dropRightPlayerSecondCard, dropRightPlayerThirdCard)
        super.initUI()
    }

    override fun setupObservers() {
        super.setupObservers()
        observe(gameViewModel.playersPositions) {
            it.forEach { (position, name) -> getPlayerNameTextView(position)?.text = name }
        }
        observe(gameViewModel.currentHandPlayerPosition) {
            setHandPlayerIcon(getHandPlayerIcon(it))
        }
    }

    override fun initializeRivalHands(isFirstHand: Boolean) = with(gameBinding) {
        frontPlayerCardsHand = TrucoFor4FrontPlayerCardsHand(
            previousCardsHand = if (isFirstHand) null else frontPlayerCardsHand,
            cardViews = frontPlayerCardViews
        )
        loadCardImages(frontPlayerCardViews, emptyList())
        leftPlayerCardsHand = TrucoFor4LeftPlayerCardsHand(
            previousCardsHand = if (isFirstHand) null else leftPlayerCardsHand,
            cardViews = leftPlayerCardViews
        )
        loadCardImages(leftPlayerCardViews, emptyList())
        rightPlayerCardsHand = TrucoFor4RightPlayerCardsHand(
            previousCardsHand = if (isFirstHand) null else rightPlayerCardsHand,
            cardViews = rightPlayerCardViews
        )
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

    override fun getPlayerBubbleWithTextView(playerPosition: TrucoPlayerPosition): Pair<View, TextView> {
        val (bubble, text) = bubbleForPosition(playerPosition)
        return requireView().findViewById<View>(bubble) to requireView().findViewById(text)
    }

    override fun bubbleForPosition(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.FRONT -> R.id.front_player_action_bubble to R.id.front_player_action_bubble_text
        TrucoPlayerPosition.LEFT -> R.id.left_player_action_bubble to R.id.left_player_action_bubble_text
        TrucoPlayerPosition.RIGHT -> R.id.right_player_action_bubble to R.id.right_player_action_bubble_text
        TrucoPlayerPosition.MY_SELF -> R.id.my_action_bubble to R.id.my_action_bubble_text
    }

    private fun getPlayerNameTextView(position: TrucoPlayerPosition) = when (position) {
        TrucoPlayerPosition.FRONT -> gameBinding.frontPlayerName
        TrucoPlayerPosition.LEFT -> gameBinding.leftPlayerName
        TrucoPlayerPosition.RIGHT -> gameBinding.rightPlayerName
        TrucoPlayerPosition.MY_SELF -> null
    }

    override fun updateCurrentTurn(playerPosition: TrucoPlayerPosition) {
        super.updateCurrentTurn(playerPosition)
        TrucoPlayerPosition.values().forEach {
            val color = if (it == playerPosition) R.color.colorPrimary else R.color.colorText
            getPlayerNameTextView(it)?.setTextColor(ContextCompat.getColor(requireContext(), color))
        }
    }

    private fun getHandPlayerIcon(playerPosition: TrucoPlayerPosition) = when (playerPosition) {
        TrucoPlayerPosition.FRONT -> gameBinding.frontPlayerHand
        TrucoPlayerPosition.LEFT -> gameBinding.leftPlayerHand
        TrucoPlayerPosition.RIGHT -> gameBinding.rightPlayerHand
        TrucoPlayerPosition.MY_SELF -> null
    }

    private fun setHandPlayerIcon(handPlayerIcon: ImageView?) {
        listOf(gameBinding.frontPlayerHand, gameBinding.leftPlayerHand, gameBinding.rightPlayerHand).forEach {
            it.isVisible = it == handPlayerIcon
        }
    }

    companion object {
        fun newInstance() = TrucoPlayFor4Fragment()
    }
}