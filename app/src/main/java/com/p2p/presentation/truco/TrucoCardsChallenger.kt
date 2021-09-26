package com.p2p.presentation.truco

import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit

object TrucoCardsChallenger {

    private val aceOfSwords = Card(1, Suit.SWORDS)
    private val aceOfClubs = Card(1, Suit.CLUBS)
    private val sevenOfSwords = Card(7, Suit.SWORDS)
    private val sevenOfClubs = Card(7, Suit.CLUBS)
    private val specialCards = listOf(aceOfSwords, aceOfClubs, sevenOfSwords, sevenOfClubs)
    private val cardNumberOrder = listOf(3, 2, 1, 12, 11, 10, 7, 6, 5, 4)

    fun getWinnerCard(cards: List<Card>) = requireNotNull(cards.minByOrNull { getCardScore(it) })

    private fun getCardScore(card: Card): Int {
        return specialCards.indexOf(card).takeIf { it >= 0 }
            ?: specialCards.size + cardNumberOrder.indexOf(card.number)
    }
}