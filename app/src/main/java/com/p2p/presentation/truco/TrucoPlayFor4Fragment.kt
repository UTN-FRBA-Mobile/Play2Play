package com.p2p.presentation.truco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.p2p.databinding.FragmentPlayTrucoFor4Binding
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4FrontPlayerCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4LeftPlayerCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4MyCardsHand
import com.p2p.presentation.truco.cards.TrucoFor4RightPlayerCardsHand
import kotlin.random.Random

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
        currentRound = mockOnCardPlayed(playingCard, currentRound) // TODO: delete
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

    // TODO: delete
    private fun mockOnCardPlayed(playingCard: TrucoCardsHand.PlayingCard, currentRound: Int): Int {
        frontPlayerCardsHand.playCard(
            cards[currentRound + 3],
            cardsImageCreator.create(cards[currentRound + 3]),
            frontPlayerDroppingPlacesViews[currentRound]
        )
        leftPlayerCardsHand.playCard(
            cards[currentRound + 6],
            cardsImageCreator.create(cards[currentRound + 6]),
            leftPlayerDroppingPlacesViews[currentRound]
        )
        rightPlayerCardsHand.playCard(
            cards[currentRound + 9],
            cardsImageCreator.create(cards[currentRound + 9]),
            rightPlayerDroppingPlacesViews[currentRound]
        )
        when (currentRound) {
            0 -> showRivalAction(
                gameBinding.leftPlayerActionBubble,
                gameBinding.leftPlayerActionBubbleText,
                TrucoAction.Envido(false)
            )
            1 -> showRivalAction(
                gameBinding.rightPlayerActionBubble,
                gameBinding.rightPlayerActionBubbleText,
                TrucoAction.Truco
            )
        }
        playingCard.view.postDelayed({ myCardsHand.takeTurn() }, 2_000)
        updateScores(Random.nextInt(1, 4), Random.nextInt(4, 10))
        finishRound(
            currentRound,
            when (currentRound) {
                0 -> TrucoRoundResult.WIN
                1 -> TrucoRoundResult.DEFEAT
                else -> TrucoRoundResult.TIE
            }
        )
        return currentRound + 1
    }

    // TODO: delete
    override fun mockReplyToMyAction(action: TrucoAction) {
        if (action in listOf(
                TrucoAction.Truco,
                TrucoAction.Retruco,
                TrucoAction.ValeCuatro
            ) || action.javaClass.simpleName.contains("envido", ignoreCase = true)
        ) {
            requireView().postDelayed(
                {
                    val useLeft = Random.nextInt(0, 10) < 5
                    showRivalAction(
                        if (useLeft) gameBinding.leftPlayerActionBubble else gameBinding.rightPlayerActionBubble,
                        if (useLeft) gameBinding.leftPlayerActionBubbleText else gameBinding.rightPlayerActionBubbleText,
                        when (action) {
                            TrucoAction.Truco -> TrucoAction.Retruco
                            TrucoAction.Retruco -> TrucoAction.ValeCuatro
                            TrucoAction.ValeCuatro -> TrucoAction.NoIDont
                            else -> TrucoAction.CustomFinalActionResponse("Quiero,\n27", hasReplication = true)
                        }
                    )
                },
                2_000
            )
        }
    }

    // TODO: delete
    override fun mockReplyToTheirAction(action: TrucoAction) {
        if (action.getMessage(requireContext()) == "Quiero,\n27") {
            gameBinding.myActionBubbleText.postDelayed(
                { showMyAction(TrucoAction.CustomFinalActionResponse("31 son\nmejores")) },
                2_000
            )
        }
    }

    companion object {

        fun newInstance() = TrucoPlayFor4Fragment()
    }
}