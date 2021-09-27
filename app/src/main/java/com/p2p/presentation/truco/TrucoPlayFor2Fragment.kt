
package com.p2p.presentation.truco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.p2p.databinding.FragmentPlayTrucoFor2Binding
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.presentation.truco.cards.TrucoSingleOpponentMyCardsHand
import com.p2p.presentation.truco.cards.TrucoSingleOpponentTheirCardsHand

class TrucoPlayFor2Fragment : TrucoFragment<FragmentPlayTrucoFor2Binding>() {

    override val gameViewModel by activityViewModels<TrucoViewModel>()
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTrucoFor2Binding =
        FragmentPlayTrucoFor2Binding::inflate

    private lateinit var theirCardsHand: TrucoCardsHand

    private lateinit var theirCardsViews: List<ImageView>
    private lateinit var theirDroppingPlacesViews: List<View>

    private var currentRound = 0 // TODO: move it to VM
    private lateinit var cards: List<Card> // TODO: move it to VM


    override fun initUI() = with(gameBinding) {
        super.initUI()
        theirCardsViews = listOf(theirLeftCard, theirMiddleCard, theirRightCard)
        theirDroppingPlacesViews = listOf(dropTheirFirstCard, dropTheirSecondCard, dropTheirThirdCard)

        cards = mockCards() // TODO: delete
        initCardsHand(cards.take(3), cards.drop(3)) // TODO: delete
        updateScores(0, 0) // TODO: delete
        takeTurn() // TODO: delete
    }

    override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {
    }

    override fun hideAllActions() {
        hideActionBubble(gameBinding.theirActionBubble, gameBinding.theirActionBubbleText)
    }

    override fun onGameEvent(event: GameEvent) {
        when (event) {
            is TrucoShowOpponentActionEvent -> showOpponentAction(event.action)
            else -> super.onGameEvent(event)
        }
    }

    private fun initCardsHand(myCards: List<Card>, theirCards: List<Card>) {
        val myPlayingCards = getPlayingCards(myCardsViews, myCards)
        val theirPlayingCards = getPlayingCards(theirCardsViews, theirCards)
        myCardsHand = TrucoSingleOpponentMyCardsHand(myPlayingCards, myDroppingPlacesViews, this@TrucoPlayFor2Fragment)
        theirCardsHand = TrucoSingleOpponentTheirCardsHand(theirPlayingCards)
    }

    private fun showOpponentAction(action: TrucoAction) = showRivalAction(
        gameBinding.theirActionBubble,
        gameBinding.theirActionBubbleText,
        action
    )

    // TODO: delete
    private fun mockCards(): List<Card> {
        val suits = listOf(Suit.SWORDS, Suit.GOLDS, Suit.CUPS, Suit.CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        val cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
        loadCardImages(myCardsViews, cards)
        loadCardImages(theirCardsViews, emptyList())
        return cards
    }

    companion object {

        fun newInstance() = TrucoPlayFor2Fragment()
    }
}