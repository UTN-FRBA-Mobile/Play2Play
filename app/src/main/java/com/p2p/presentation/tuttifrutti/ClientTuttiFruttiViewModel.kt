package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.MessageReceived
import com.p2p.model.tuttifrutti.TuttiFruttiStartGame
import com.p2p.presentation.basegame.ConnectionType

class ClientTuttiFruttiViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository
) : TuttiFruttiViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository
) {

    override fun receiveMessage(messageReceived: MessageReceived) {
        super.receiveMessage(messageReceived)
        when (val message = messageReceived.message) {
            is TuttiFruttiStartGame -> {
                lettersByRound = message.letters
                setTotalRounds(message.letters.count())
                setCategoriesToPlay(message.categories)
                startGame()
            }
        }
    }

    override fun startGame() = goToPlay()

}
