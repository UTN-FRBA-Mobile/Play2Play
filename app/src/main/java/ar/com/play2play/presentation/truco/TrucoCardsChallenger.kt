package ar.com.play2play.presentation.truco

import ar.com.play2play.model.truco.Card
import ar.com.play2play.model.truco.Suit

object TrucoCardsChallenger {

    val aceOfSwords = Card(1, Suit.SWORDS)
    private val aceOfClubs = Card(1, Suit.CLUBS)
    private val sevenOfSwords = Card(7, Suit.SWORDS)
    private val sevenOfGolds = Card(7, Suit.GOLDS)
    private val specialCards = listOf(aceOfSwords, aceOfClubs, sevenOfSwords, sevenOfGolds)
    private val cardNumberOrder = listOf(3, 2, 1, 12, 11, 10, 7, 6, 5, 4)

    fun getWinnerCards(cards: List<Card>) = cards
        .groupBy { getCardScore(it) }
        .toList()
        .minByOrNull { (score, _) -> score }!!
        .second

    private fun getCardScore(card: Card): Int {
        return specialCards.indexOf(card).takeIf { it >= 0 }
            ?: specialCards.size + cardNumberOrder.indexOf(card.number)
    }
}
