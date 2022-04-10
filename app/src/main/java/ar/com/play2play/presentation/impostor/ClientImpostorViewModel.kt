package ar.com.play2play.presentation.impostor

import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.impostor.ImpostorData
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.impostor.message.ImpostorAssignWord
import ar.com.play2play.model.impostor.message.ImpostorEndGame
import ar.com.play2play.presentation.basegame.ConnectionType
import ar.com.play2play.presentation.basegame.KillGame

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

    private fun assignWordAndStart(message: ImpostorAssignWord) = with(message){
        _impostorData.value = ImpostorData(impostor, word, wordTheme, isImpostor = impostor == userName)
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
