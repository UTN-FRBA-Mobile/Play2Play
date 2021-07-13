package com.p2p.presentation.impostor

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.impostor.message.ImpostorAssignWord
import com.p2p.model.impostor.message.ImpostorEndGame
import com.p2p.presentation.basegame.ConnectionType

class ServerImpostorViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : ImpostorViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository
) {

    fun createGame(word: String) {
        val impostor = selectImpostor()
        _impostor.value = impostor
        _keyWord.value = word
        connection.write(
            ImpostorAssignWord(
                word,
                impostor
            )
        )
        closeDiscovery()
        goToPlay()
    }

    fun endGame(){
        connection.write(ImpostorEndGame())
    }

    private fun selectImpostor(): String{
        val players = requireNotNull(otherPlayers()) { "At this instance at least one player must be connected" }
        return players.shuffled().first()
    }

}
