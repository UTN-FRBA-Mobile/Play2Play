package com.p2p.presentation.truco

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.Card
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.Suit.*
import com.p2p.model.truco.message.TrucoCardsMessage
import com.p2p.presentation.basegame.ConnectionType

class ServerTrucoViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : TrucoViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository
) {
    /** Deck of cards being used by all players in a hand  */
    private var cards = listOf<Card>()

    /** Be careful: this will be called for every client on a broadcast. */
    override fun onSentSuccessfully(conversation: Conversation) {
        super.onSentSuccessfully(conversation)
    }

    override fun startGame() {
        closeDiscovery()
        handOutCards()
        goToPlay()
    }

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            // TODO: Implement messages handling
        }
    }

    /** Sends all client players the cards for each one and picks self cards. */
    override fun handOutCards() {
        mixDeck()
        val playersWithCards = connectedPlayers
            .filterNot { it.first == MYSELF_PEER_ID }
            .map { player -> PlayerWithCards(player.second, cardsForPlayer()) }
        _myCards.value = cardsForPlayer()
        connection.write(TrucoCardsMessage(playersWithCards))
    }

    private fun cardsForPlayer(): List<Card> {
        val hand = cards.take(3)
        cards = cards.minus(hand)
        return hand
    }

    private fun mixDeck() {
        val suits = listOf(SWORDS, GOLDS, CUPS, CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
    }

}
