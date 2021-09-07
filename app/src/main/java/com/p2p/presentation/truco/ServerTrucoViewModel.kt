package com.p2p.presentation.truco

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.PlayerTeam
import com.p2p.model.truco.message.TrucoStartGameMessage
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
    /** Be careful: this will be called for every client on a broadcast. */
    override fun onSentSuccessfully(conversation: Conversation) {
        super.onSentSuccessfully(conversation)
        when (conversation.lastMessage) {
            is TrucoStartGameMessage -> if (!gameAlreadyStarted) {
                goToPlay() // starts the game when the first StartGame message was sent successfully.
            }
        }
    }

    override fun startGame() {
        playersTeams = setPlayersTeams()
        connection.write(
            TrucoStartGameMessage(playersTeams)
        )
        closeDiscovery()
    }

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            // TODO: Implement messages handling
        }
    }

    private fun setPlayersTeams(): List<PlayerTeam> {
        val playersTeams = mutableListOf<PlayerTeam>()
        players.requireValue().take(totalPlayers.requireValue()).forEachIndexed { index, element ->
            val teamNumber = if(index % 2 == 0) 1 else 2
            playersTeams.add(PlayerTeam(element, teamNumber, index == 0))
        }
        return playersTeams
    }
}
