package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
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
    private var saidEnoughPeer: Long? = null

    /** Be careful: this will be called for every client on a broadcast. */
    override fun onSentSuccessfully(conversation: Conversation) {
        super.onSentSuccessfully(conversation)
        when (conversation.lastMessage) {
            is TuttiFruttiStartGame -> if (!gameAlreadyStarted) {
                goToPlay() // starts the game when the first StartGame message was sent successfully.
                gameAlreadyStarted = true
            }
        }
    }

    override fun startGame() {
        lettersByRound = getRandomLetters()
        connection.write(TuttiFruttiStartGame(lettersByRound, categoriesToPlay.requireValue()))
        closeDiscovery()
        goToPlay()
    }

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TuttiFruttiSendWordsMessage -> acceptWords(conversation.peer, message.words)
        }
    }

    override fun sendWords(categoriesWords: LinkedHashMap<Category, String>) = acceptWords(MYSELF_PEER_ID, categoriesWords)

    override fun enoughForMeEnoughForAll() {
        saidEnough(MYSELF_PEER_ID)
        super.enoughForMeEnoughForAll()
        stopRound()
    }

    override fun onReceiveEnoughForAll(conversation: Conversation) {
        saidEnough(conversation.peer)
        super.onReceiveEnoughForAll(conversation)
    }

    private fun saidEnough(peer: Long) {
        saidEnoughPeer = peer
        _finishedRoundInfos.value = emptyList()
    }

    private fun acceptWords(peer: Long, categoriesWords: LinkedHashMap<Category, String>) {
        _finishedRoundInfos.value = _finishedRoundInfos.requireValue() + FinishedRoundInfo(
            player = getPlayerById(peer),
            categoriesWords = categoriesWords,
            saidEnough = peer == saidEnoughPeer
        )
        goToReviewIfCorresponds()
    }

    private fun getRandomLetters(): List<Char> =
        availableLetters.toList().shuffled().take(totalRounds.requireValue())

    private fun goToReviewIfCorresponds() {
        if (finishedRoundInfos.requireValue().size == connectedPlayers.size) {
            // When all the players send their words, go to the review and clean the players round words.
            dispatchSingleTimeEvent(GoToReview)
        }
    }
}
