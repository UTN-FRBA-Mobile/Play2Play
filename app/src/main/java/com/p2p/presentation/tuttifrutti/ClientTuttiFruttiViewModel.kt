package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.ConversationMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
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

    private var stopRoundConversationMessage: ConversationMessage? = null

    override fun onSentSuccessfully(conversationMessage: ConversationMessage) {
        when (conversationMessage.message) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> receiveMessage(conversationMessage)
        }
        super.onSentSuccessfully(conversationMessage)
    }

    override fun onReceiveEnoughForAll(conversationMessage: ConversationMessage) {
        stopRoundConversationMessage = conversationMessage
        super.onReceiveEnoughForAll(conversationMessage)
    }

    override fun sendWords(categoriesWords: Map<Category, String>) {
        stopRoundConversationMessage?.let { connection.talk(it, TuttiFruttiSendWordsMessage(categoriesWords)) }
    }
}
