package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.MessageReceived
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.tuttifrutti.create.categories.Category

class ServerTuttiFruttiViewModel(
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

    private var categoriesWordsPerPlayer = mutableMapOf<Long, Map<Category, String>>()

    override fun receiveMessage(messageReceived: MessageReceived) {
        super.receiveMessage(messageReceived)
        when (val message = messageReceived.message) {
            is TuttiFruttiSendWordsMessage -> acceptWords(messageReceived, message.words)
        }
    }

    override fun sendWords(categoriesWords: Map<Category, String>) {
        categoriesWordsPerPlayer[MYSELF_ID] = categoriesWords
        goToReviewIfCorresponds()
    }

    private fun acceptWords(messageReceived: MessageReceived, categoriesWords: Map<Category, String>) {
        categoriesWordsPerPlayer[messageReceived.senderId] = categoriesWords
        goToReviewIfCorresponds()
    }

    private fun goToReviewIfCorresponds() {
        if (categoriesWordsPerPlayer.size == connectedPlayers.size) {
            // When all the players send their words, go to the review and clean the players round words.
            val finishedRoundInfos = categoriesWordsPerPlayer.map { (playerId, categoriesWords) ->
                FinishedRoundInfo(
                    player = connectedPlayers.first { it.first == playerId }.second,
                    categoriesWords = categoriesWords
                )
            }
            dispatchSingleTimeEvent(GoToReview(finishedRoundInfos))
            categoriesWordsPerPlayer = mutableMapOf()
            _isLoading.value = false
        }
    }
}
