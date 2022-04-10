package ar.com.play2play.presentation.truco

import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.base.message.ClientHandshakeMessage
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.truco.Card
import ar.com.play2play.model.truco.PlayerWithCards
import ar.com.play2play.model.truco.Suit.CLUBS
import ar.com.play2play.model.truco.Suit.CUPS
import ar.com.play2play.model.truco.Suit.GOLDS
import ar.com.play2play.model.truco.Suit.SWORDS
import ar.com.play2play.model.truco.TeamPlayer
import ar.com.play2play.model.truco.message.*
import ar.com.play2play.presentation.basegame.ConnectionType
import ar.com.play2play.presentation.extensions.requireValue

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

    override fun startGame(players: List<String>) {
        _players.value = players
        setPlayers(createPlayersTeams())
        setHandPlayer(teamPlayers[0])
        connection.write(
            TrucoStartGameMessage(teamPlayers, totalPlayers.requireValue(), totalPoints.requireValue())
        )
        handOutCards()
        goToPlayTruco()
    }

    override fun onClientHandshake(message: ClientHandshakeMessage, conversation: Conversation): Boolean {
        val hasJoined = super.onClientHandshake(message, conversation)
        if (gameAlreadyStarted && hasJoined) {
            val welcomeBack = TrucoWelcomeBack(
                teamPlayers,
                totalPlayers.requireValue(),
                totalPoints.requireValue(),
                mapOf(myTeamPlayer.team to ourScore.requireValue(), rivalTeam to theirScore.requireValue()),
                handPlayer
            )
            connection.talk(conversation, welcomeBack)
            if (teamPlayers.size == players.requireValue().size) {
                handOutCards()
            }
        }
        return hasJoined
    }

    private fun createPlayersTeams(): List<TeamPlayer> {
        return players.requireValue().take(totalPlayers.requireValue()).mapIndexed { index, playerName ->
            val teamNumber = index % PLAYERS_PER_TEAM + 1
            TeamPlayer(playerName, teamNumber)
        }
    }

    /** Sends all client players the cards for each one and picks self cards. */
    override fun handOutCards() {
        mixDeck()
        cardsByPlayer = connectedPlayers.map { player -> PlayerWithCards(player.second, cardsForPlayer()) }
        newHand(cardsByPlayer.first { it.name == userName }.cards)
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
