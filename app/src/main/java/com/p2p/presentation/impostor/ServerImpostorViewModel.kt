package com.p2p.presentation.impostor

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartGameMessage
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

    override fun createGame(keyWord: String) {
        super.createGame(keyWord)
        //TODO bren do logic
        connection.write(
            //TODO replace
            TuttiFruttiStartGameMessage(
                emptyList(),
                emptyList()
            )
        )
        closeDiscovery()
    }

}
