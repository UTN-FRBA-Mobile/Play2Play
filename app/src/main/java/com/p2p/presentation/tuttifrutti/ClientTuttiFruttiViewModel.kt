package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.MessageReceived
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.tuttifrutti.create.categories.Category

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

    private var stopRoundMessageReceived: MessageReceived? = null

    override fun stopRound(messageReceived: MessageReceived) {
        stopRoundMessageReceived = messageReceived
        super.stopRound(messageReceived)
    }

    override fun sendWords(categoriesWords: Map<Category, String>) {
        stopRoundMessageReceived?.let { connection.answer(it, TuttiFruttiSendWordsMessage(categoriesWords)) }
    }
}
