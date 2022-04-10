package ar.com.play2play.model.truco

data class PlayerWithCards(val name: String, val cards: List<Card>)

data class Card(val number: Int, val suit: Suit) {

    companion object {
        fun unknown() = Card(-1, Suit.SWORDS)
    }
}
