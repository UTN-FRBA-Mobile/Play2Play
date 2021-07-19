package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingSource
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.message.FinalScoreMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartGameMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartRoundMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.tuttifrutti.create.categories.Category

class ClientTuttiFruttiViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : TuttiFruttiViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository
) {

    private var enoughForAllConversation: Conversation? = null

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TuttiFruttiStartGameMessage -> {
                lettersByRound = message.letters
                setTotalRounds(message.letters.count())
                setCategoriesToPlay(message.categories)
                startGame()
            }
            is TuttiFruttiStartRoundMessage -> startRound()
            is FinalScoreMessage -> {
                setFinalScores(message.playersScores)
                goToFinalScore()
            }
        }
    }

    override fun onSentSuccessfully(conversation: Conversation) {
        when (conversation.lastMessage) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> receiveMessage(conversation)
        }
        super.onSentSuccessfully(conversation)
    }

    override fun enoughForMeEnoughForAll() {
        startLoading(loadingTextRepository.getText(LoadingSource.MessageType.TF_WAITING_FOR_REVIEW))
        super.enoughForMeEnoughForAll()
    }

    override fun onReceiveEnoughForAll(conversation: Conversation) {
        enoughForAllConversation = conversation
        startLoading(loadingTextRepository.getText(LoadingSource.MessageType.TF_WAITING_FOR_REVIEW))
        super.onReceiveEnoughForAll(conversation)
    }

    override fun sendWords(categoriesWords: LinkedHashMap<Category, String>) {
        enoughForAllConversation?.let {
            connection.talk(
                it,
                TuttiFruttiSendWordsMessage(categoriesWords)
            )
        }
        enoughForAllConversation = null
    }

    override fun startGame() = goToPlay()

    override fun startRound() = goToPlay()

}
