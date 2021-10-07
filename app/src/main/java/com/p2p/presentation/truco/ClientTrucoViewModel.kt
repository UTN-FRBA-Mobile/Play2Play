package com.p2p.presentation.truco

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.message.TrucoStartGameMessage
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.message.TrucoCardsMessage
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
            is TrucoStartGameMessage -> {
                playersTeams = message.playersTeams
                setTotalPlayers(message.totalPlayers)
                setTotalPoints(message.totalPoints)
                startGame()
            }
            is TrucoCardsMessage -> onRecieveCards(message.cardsForPlayers)
        }
    }

    override fun startGame() = goToPlay()

    private fun onRecieveCards(playersWithCards: List<PlayerWithCards>) {
        cardsByPlayer = playersWithCards
        pickSelfCards(playersWithCards)
    }
    private fun pickSelfCards(playersWithCards: List<PlayerWithCards>) {
        _myCards.value = playersWithCards.first { it.player == userName }.cards
    }

}
