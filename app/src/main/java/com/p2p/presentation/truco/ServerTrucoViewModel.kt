package com.p2p.presentation.truco

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.truco.Card
import com.p2p.model.truco.PlayerTeam
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.Suit.CLUBS
import com.p2p.model.truco.Suit.CUPS
import com.p2p.model.truco.Suit.GOLDS
import com.p2p.model.truco.Suit.SWORDS
import com.p2p.model.truco.message.*
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.extensions.requireValue

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

    override fun startGame() {
        playersTeams = setPlayersTeams()
        _firstHandPlayer.value = playersTeams[0]
        connection.write(
            TrucoStartGameMessage(playersTeams, totalPlayers.requireValue(), totalPoints.requireValue())
        )
        closeDiscovery()
        handOutCards()
        goToPlay()
    }

    private fun setPlayersTeams(): List<PlayerTeam> {
        return players.requireValue().take(totalPlayers.requireValue()).mapIndexed { index, element ->
            val teamNumber = index % PLAYERS_PER_TEAM + 1
            PlayerTeam(element, teamNumber)
        }
    }

    /** Sends all client players the cards for each one and picks self cards. */
    override fun handOutCards() {
        mixDeck()
        cardsByPlayer = connectedPlayers
            .map { player -> PlayerWithCards(player.second, cardsForPlayer()) }
        val myCards = cardsByPlayer
            .first { it.player == userName }
        _myCards.value = myCards.cards
        connection.write(TrucoCardsMessage(cardsByPlayer))
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

    companion object {
        const val PLAYERS_PER_TEAM = 2
    }
}
