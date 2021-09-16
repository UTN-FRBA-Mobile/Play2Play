package com.p2p.model.truco

data class PlayerWithCards(val player: String, val cards: List<Card>)

data class Card(val number: Int, val suit: Suit)