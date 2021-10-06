package com.p2p.presentation.truco.envidoCalculator

import com.p2p.model.truco.Card

object EnvidoPointsCalculator {

    private val zeroCards = listOf(10, 11, 12)

    fun getPoints(cards: List<Card>): Int = cards
        .groupBy { it.suit }
        .toList()
        .map { (_, cardsBySuite) ->
            val cardValues = cardsBySuite.map { getCardValue(it) }
            when (cardValues.size) {
                1 -> cardValues.first()
                else -> 20 + cardValues.sorted().take(2).sum()
            }
        }.maxOrNull()!!

    private fun getCardValue(card: Card): Int =
        if (zeroCards.contains(card.number)) 0 else card.number

}
