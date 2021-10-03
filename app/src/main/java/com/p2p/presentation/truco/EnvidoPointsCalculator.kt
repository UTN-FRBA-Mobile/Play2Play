package com.p2p.presentation.truco

import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit

object EnvidoPointsCalculator {

    private val zeroCards = listOf(10, 11, 12)

    fun getPoints(cards: List<Card>): Int = cards
        .groupBy { it.suit }
        .toList()
        .map { (_, cardsBySuite) ->
            with(cardsBySuite.map { getCardValue(it) }) {
                when (this.size) {
                    1 -> this.first()
                    else -> 20 + this.sorted().take(2).sum()
                }
            }
        }

        .maxOrNull()!!

    private fun getCardValue(card: Card): Int =
        if (zeroCards.contains(card.number)) 0 else card.number

}
