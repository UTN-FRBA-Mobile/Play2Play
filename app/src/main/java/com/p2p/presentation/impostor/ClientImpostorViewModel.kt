package com.p2p.presentation.impostor

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.impostor.ImpostorData
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.impostor.message.ImpostorAssignWord
import com.p2p.model.impostor.message.ImpostorEndGame
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.KillGame

class ClientImpostorViewModel(
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

    fun assignWordAndStart(message: ImpostorAssignWord) = with(message){
        _impostorData.value = ImpostorData(impostor, word, isImpostor = impostor == userName)
        startGame()
    }

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is ImpostorAssignWord -> assignWordAndStart(message)
            is ImpostorEndGame -> dispatchSingleTimeEvent(KillGame)
        }
    }

}
