package com.p2p.presentation.tuttifrutti

import androidx.lifecycle.viewModelScope
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingSource
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.message.FinalScoreMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartGameMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartRoundMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.KillGame
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.tuttifrutti.create.categories.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServerTuttiFruttiViewModel(
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
    private var roundAlreadyStarted = false
    private var saidEnoughPeer: Long? = null
    private var waitingWordsJob: Job? = null

    /** Be careful: this will be called for every client on a broadcast. */
    override fun onSentSuccessfully(conversation: Conversation) {
        super.onSentSuccessfully(conversation)
        when (conversation.lastMessage) {
            is TuttiFruttiStartGameMessage -> if (!gameAlreadyStarted) {
                goToPlay() // starts the game when the first StartGame message was sent successfully.
            }
            is TuttiFruttiStartRoundMessage -> if (!roundAlreadyStarted) {
                goToPlay()
                roundAlreadyStarted = true
            }
        }
    }

    override fun startGame() {
        lettersByRound = getRandomLetters()
        connection.write(
            TuttiFruttiStartGameMessage(
                lettersByRound,
                categoriesToPlay.requireValue()
            )
        )
        closeDiscovery()
    }

    override fun startRound() {
        connection.write(TuttiFruttiStartRoundMessage())
        roundAlreadyStarted = false
    }

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TuttiFruttiSendWordsMessage -> acceptWords(conversation.peer, message.words)
        }
    }

    override fun sendWords(categoriesWords: LinkedHashMap<Category, String>) =
        acceptWords(MYSELF_PEER_ID, categoriesWords)

    override fun enoughForMeEnoughForAll() {
        saidEnough(MYSELF_PEER_ID)
        super.enoughForMeEnoughForAll()
        stopRound()
    }

    override fun onReceiveEnoughForAll(conversation: Conversation) {
        saidEnough(conversation.peer)
        super.onReceiveEnoughForAll(conversation)
    }

    override fun calculateFinalScores() {
        super.calculateFinalScores()
        connection.write(FinalScoreMessage(finalScores.requireValue()))
    }

    private fun saidEnough(peer: Long) {
        startLoading(loadingTextRepository.getText(LoadingSource.MessageType.TF_WAITING_FOR_WORDS))
        saidEnoughPeer = peer
        _finishedRoundInfos.value = emptyList()
        waitingWordsJob = viewModelScope.launch(Dispatchers.Default) {
            delay(WAITING_WORDS_TIMEOUT_MS)
            withContext(Dispatchers.Main) { stopAcceptingWords() }
        }
    }

    private fun acceptWords(peer: Long, categoriesWords: LinkedHashMap<Category, String>) {
        _finishedRoundInfos.value = _finishedRoundInfos.requireValue() + FinishedRoundInfo(
            peer = peer,
            player = getPlayerById(peer),
            categoriesWords = categoriesWords,
            saidEnough = peer == saidEnoughPeer
        )
        goToReviewIfCorresponds()
    }

    private fun stopAcceptingWords() {
        val removedConnectedPlayers = connectedPlayers.filterNot { (peer, _) ->
            finishedRoundInfos.requireValue().any { it.peer == peer }
        }
        connectedPlayers = connectedPlayers - removedConnectedPlayers
        removedConnectedPlayers.forEach { (peer, _) -> connection.killPeer(peer) }
        if (connectedPlayers.size == 1) {
            dispatchErrorScreen(SinglePlayerOnGame { dispatchSingleTimeEvent(KillGame) })
        } else {
            goToReviewIfCorresponds()
        }
    }

    private fun getRandomLetters(): List<Char> =
        availableLetters.toList().shuffled().take(totalRounds.requireValue())

    private fun goToReviewIfCorresponds() {
        if (finishedRoundInfos.requireValue().size == connectedPlayers.size) {
            waitingWordsJob?.cancel()
            waitingWordsJob = null
            // When all the players send their words, go to the review and clean the players round words.
            dispatchSingleTimeEvent(GoToReview)
        }
    }

    companion object {

        private const val WAITING_WORDS_TIMEOUT_MS = 45_000L
    }
}
