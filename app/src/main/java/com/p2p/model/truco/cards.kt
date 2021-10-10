package com.p2p.model.truco

data class PlayerWithCards(val player: String, val cards: List<Card>)

open class Card(val number: Int, val suit: Suit) {

    companion object {

        fun unknown() = Card(-1, Suit.SWORDS)
    }
}


object EmptyCard: Card(0, Suit.CLUBS)