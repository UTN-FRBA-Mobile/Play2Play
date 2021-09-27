package com.p2p.presentation.truco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.p2p.databinding.FragmentPlayTrucoFor4Binding
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import com.p2p.presentation.truco.cards.*

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

    private var currentRound = 0 // TODO: move it to VM
    private lateinit var cards: List<Card> // TODO: move it to VM

    override fun initUI() = with(gameBinding) {
        super.initUI()
        frontPlayerCardViews = listOf(frontPlayerLeftCard, frontPlayerMiddleCard, frontPlayerRightCard)
        frontPlayerDroppingPlacesViews =
            listOf(dropFrontPlayerFirstCard, dropFrontPlayerSecondCard, dropFrontPlayerThirdCard)
        leftPlayerCardViews = listOf(leftPlayerLeftCard, leftPlayerMiddleCard, leftPlayerRightCard)
        leftPlayerDroppingPlacesViews =
            listOf(dropLeftPlayerFirstCard, dropLeftPlayerSecondCard, dropLeftPlayerThirdCard)
        rightPlayerCardViews = listOf(rightPlayerLeftCard, rightPlayerMiddleCard, rightPlayerRightCard)
        rightPlayerDroppingPlacesViews =
            listOf(dropRightPlayerFirstCard, dropRightPlayerSecondCard, dropRightPlayerThirdCard)

        cards = mockCards() // TODO: delete
        initCardsHand(cards.take(3), cards.drop(3), cards.drop(6), cards.drop(9)) // TODO: delete
        updateScores(0, 0) // TODO: delete
        takeTurn() // TODO: delete
    }

    override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {

    }

    override fun hideAllActions() {
        hideActionBubble(gameBinding.leftPlayerActionBubble, gameBinding.leftPlayerActionBubbleText)
        hideActionBubble(gameBinding.frontPlayerActionBubble, gameBinding.frontPlayerActionBubbleText)
        hideActionBubble(gameBinding.rightPlayerActionBubble, gameBinding.rightPlayerActionBubbleText)
    }

    private fun initCardsHand(
        myCards: List<Card>,
        frontPlayerCards: List<Card>,
        leftPlayerCards: List<Card>,
        rightPlayerCards: List<Card>
    ) {
        val myPlayingCards = getPlayingCards(myCardsViews, myCards)
        val frontPlayerPlayingCards = getPlayingCards(frontPlayerCardViews, frontPlayerCards)
        val leftPlayerPlayingCards = getPlayingCards(leftPlayerCardViews, leftPlayerCards)
        val rightPlayerPlayingCards = getPlayingCards(rightPlayerCardViews, rightPlayerCards)
        myCardsHand = TrucoFor4MyCardsHand(myPlayingCards, myDroppingPlacesViews, this)
        frontPlayerCardsHand = TrucoFor4FrontPlayerCardsHand(frontPlayerPlayingCards)
        leftPlayerCardsHand = TrucoFor4LeftPlayerCardsHand(leftPlayerPlayingCards)
        rightPlayerCardsHand = TrucoFor4RightPlayerCardsHand(rightPlayerPlayingCards)
    }

    // TODO: delete
    private fun mockCards(): List<Card> {
        val suits = listOf(Suit.SWORDS, Suit.GOLDS, Suit.CUPS, Suit.CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        val cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
        loadCardImages(myCardsViews, cards)
        loadCardImages(frontPlayerCardViews, emptyList())
        loadCardImages(leftPlayerCardViews, emptyList())
        loadCardImages(rightPlayerCardViews, emptyList())
        return cards
    }

    companion object {

        fun newInstance() = TrucoPlayFor4Fragment()
    }
}