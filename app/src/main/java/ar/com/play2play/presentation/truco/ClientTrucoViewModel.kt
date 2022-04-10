package ar.com.play2play.presentation.truco

import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.truco.PlayerWithCards
import ar.com.play2play.model.truco.TeamPlayer
import ar.com.play2play.model.truco.message.TrucoCardsMessage
import ar.com.play2play.model.truco.message.TrucoStartGameMessage
import ar.com.play2play.model.truco.message.TrucoWelcomeBack
import ar.com.play2play.presentation.basegame.ConnectionType

class ClientTrucoViewModel(
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

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TrucoStartGameMessage -> startGame(
                message.teamPlayers,
                message.totalPlayers,
                message.totalPoints,
                message.teamPlayers.first()
            )
            is TrucoCardsMessage -> onReceiveCards(message.cardsForPlayers)
            is TrucoWelcomeBack -> welcomeBack(message)
        }
    }

    override fun startGame(players: List<String>) {
        goToPlayTruco()
    }

    private fun onReceiveCards(playersWithCards: List<PlayerWithCards>) {
        cardsByPlayer = playersWithCards
        newHand(playersWithCards.first { it.name == userName }.cards)
        pickSelfCards(playersWithCards)
    }

    private fun pickSelfCards(playersWithCards: List<PlayerWithCards>) {
        _myCards.value = playersWithCards.first { it.name == userName }.cards
    }

    private fun startGame(
        teamPlayers: List<TeamPlayer>,
        totalPlayers: Int,
        totalPoints: Int,
        handPlayer: TeamPlayer
    ) {
        setPlayers(teamPlayers)
        setTotalPlayers(totalPlayers)
        setTotalPoints(totalPoints)
        setHandPlayer(handPlayer)
        startGame(emptyList())
    }

    private fun welcomeBack(message: TrucoWelcomeBack) {
        startGame(message.teamPlayers, message.totalPlayers, message.totalPoints, message.handPlayer)
        _ourScore.value = message.scores.getValue(myTeamPlayer.team)
        _theirScore.value = message.scores.getValue(rivalTeam)
    }
}
