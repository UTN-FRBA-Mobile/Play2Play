package com.p2p.presentation.truco

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.TeamPlayer
import com.p2p.model.truco.message.TrucoCardsMessage
import com.p2p.model.truco.message.TrucoStartGameMessage
import com.p2p.model.truco.message.TrucoWelcomeBack
import com.p2p.presentation.basegame.ConnectionType

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
        this.handPlayer = handPlayer
        startGame(emptyList())
    }

    private fun welcomeBack(message: TrucoWelcomeBack) {
        startGame(message.teamPlayers, message.totalPlayers, message.totalPoints, message.handPlayer)
        _ourScore.value = message.scores.getValue(myTeamPlayer.team)
        _theirScore.value = message.scores.getValue(rivalTeam)
    }
}
