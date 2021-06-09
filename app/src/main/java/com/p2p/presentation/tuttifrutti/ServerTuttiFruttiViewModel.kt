package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.ConversationMessage
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.TuttiFruttiStartGame
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.extensions.requireValue
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
    private var gameAlreadyStarted = false

    private var categoriesWordsPerPlayer = mutableMapOf<Long, Map<Category, String>>()

    /** Be careful: this will be called for every client on a broadcast. */
    override fun onSentSuccessfully(conversationMessage: ConversationMessage) {
        super.onSentSuccessfully(conversationMessage)
        when (conversationMessage.message) {
            is TuttiFruttiStartGame -> if (!gameAlreadyStarted) {
                goToPlay() // starts the game when the first StartGame message was sent successfully.
                gameAlreadyStarted = true
            }
        }
    }

    override fun startGame() {
        lettersByRound = getRandomLetters()
        connection.write(TuttiFruttiStartGame(lettersByRound, categoriesToPlay.requireValue()))
    }

    override fun receiveMessage(conversationMessage: ConversationMessage) {
        super.receiveMessage(conversationMessage)
        when (val message = conversationMessage.message) {
            is TuttiFruttiSendWordsMessage -> acceptWords(conversationMessage, message.words)
        }
    }

    override fun sendWords(categoriesWords: Map<Category, String>) {
        categoriesWordsPerPlayer[MYSELF_ID] = categoriesWords
        goToReviewIfCorresponds()
    }

    private fun acceptWords(conversationMessage: ConversationMessage, categoriesWords: Map<Category, String>) {
        categoriesWordsPerPlayer[conversationMessage.peer] = categoriesWords
        goToReviewIfCorresponds()
    }

    private fun getRandomLetters(): List<Char> =
        availableLetters.toList().shuffled().take(totalRounds.requireValue())

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
