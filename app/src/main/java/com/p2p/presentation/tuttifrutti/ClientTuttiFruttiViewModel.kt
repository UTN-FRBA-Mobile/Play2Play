package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.TuttiFruttiStartGame
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

    private var stopRoundConversation: Conversation? = null

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TuttiFruttiStartGame -> {
                lettersByRound = message.letters
                setTotalRounds(message.letters.count())
                setCategoriesToPlay(message.categories)
                startGame()
            }
        }
    }

    override fun onSentSuccessfully(conversation: Conversation) {
        when (conversation.lastMessage) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> receiveMessage(conversation)
        }
        super.onSentSuccessfully(conversation)
    }

    override fun onReceiveEnoughForAll(conversation: Conversation) {
        stopRoundConversation = conversation
        super.onReceiveEnoughForAll(conversation)
    }

    override fun sendWords(categoriesWords: Map<Category, String>) {
        stopRoundConversation?.let { connection.talk(it, TuttiFruttiSendWordsMessage(categoriesWords)) }
    }

    override fun startGame() = goToPlay()
}
