package com.p2p.presentation.tuttifrutti

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.basegame.KillGame
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.presentation.tuttifrutti.finalscore.TuttiFruttiFinalScore

abstract class TuttiFruttiViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : GameViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository,
    Game.TUTTI_FRUTTI
) {

    protected lateinit var lettersByRound: List<Char>

    protected val _totalRounds = MutableLiveData<Int>()
    val totalRounds: LiveData<Int> = _totalRounds

    protected val _finishedRoundInfos = MutableLiveData(setOf<FinishedRoundInfo>())
    val finishedRoundInfos: LiveData<Set<FinishedRoundInfo>> = _finishedRoundInfos

    private val _finishedRoundsPointsInfos = MutableLiveData(listOf<FinishedRoundPointsInfo>())
    val finishedRoundsPointsInfos: LiveData<List<FinishedRoundPointsInfo>> =
        _finishedRoundsPointsInfos

    private val _categoriesToPlay = MutableLiveData<List<Category>>()
    val categoriesToPlay: LiveData<List<Category>> = _categoriesToPlay

    private val _actualRound = MutableLiveData<RoundInfo>()
    val actualRound: LiveData<RoundInfo> = _actualRound

    private val _finalScores = MutableLiveData<List<TuttiFruttiFinalScore>>()
    val finalScores: LiveData<List<TuttiFruttiFinalScore>> = _finalScores

    /** Set the categories selected by the user when creating the game . */
    fun setCategoriesToPlay(categories: List<Category>) {
        _categoriesToPlay.value = categories
    }

    fun setTotalRounds(totalRounds: Int) {
        _totalRounds.value = totalRounds
    }

    fun setFinishedRoundPointsInfos(finishedRoundPointsInfo: List<FinishedRoundPointsInfo>) {
        _finishedRoundsPointsInfos.value =
            _finishedRoundsPointsInfos.value?.plus(finishedRoundPointsInfo)
    }

    /**
     * Goes to the final scores view or starts a new round when corresponds.
     * */
    fun startRoundOrFinishGame() {
        if (actualRound.requireValue().number == totalRounds.requireValue()) { // last round
            goToFinalScore()
        } else {
            startRound()
        }
    }

    fun generateNextRoundValues() {
        val round = (actualRound.value?.number ?: 0) + 1
        _actualRound.value = RoundInfo(lettersByRound[round - 1], round)
    }

    abstract fun startRound()

    /**
     * Enough for me enough for all will say to the room that the round is finished.
     *
     * The client and the server will handle different the invocation of this method:
     * - The client will just send the message and when it's sent, it'll stop the round
     *   (that's because it needs the conversation started with the server to send their words).
     * - The server will stop the round immediately when this is invoked because it doesn't need
     *   to do anything more, just wait for the others words.
     */
    @CallSuper
    open fun enoughForMeEnoughForAll() {
        connection.write(TuttiFruttiEnoughForMeEnoughForAllMessage())
    }

    abstract fun sendWords(categoriesWords: LinkedHashMap<Category, String>)

    /**
     * The server calculates the final scores and send them to the clients.
     * */
    open fun calculateFinalScores() {
        if (finalScores.value?.isEmpty() != false) {
            _finalScores.value = finishedRoundsPointsInfos.requireValue().groupBy { roundInfo -> roundInfo.player }
                .entries
                .map { entry ->
                    TuttiFruttiFinalScore(
                        entry.key,
                        entry.value.map { roundPoints -> roundPoints.totalPoints }.sum()
                    )
                }
                .sortedByDescending { results -> results.finalScore }
        }
    }

    fun setFinalScores(scores: List<TuttiFruttiFinalScore>) {
        _finalScores.value = scores
    }

    override fun goToPlay() {
        gameAlreadyStarted = true
        super.goToPlay()
    }

    @CallSuper
    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (conversation.lastMessage) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> onReceiveEnoughForAll(conversation)
        }
    }

    protected open fun onReceiveEnoughForAll(conversation: Conversation) {
        stopRound()
    }

    override fun onClientConnectionLost(peerId: Long) {
        super.onClientConnectionLost(peerId)
        if (gameAlreadyStarted && connectedPlayers.size == 1) {
            dispatchErrorScreen(SinglePlayerOnGame {
                dispatchSingleTimeEvent(KillGame)
            })
        }
    }

    protected fun stopRound() = dispatchSingleTimeEvent(ObtainWords)

    protected fun goToFinalScore() {
        gameAlreadyFinished = true
        dispatchSingleTimeEvent(GoToFinalScore)
    }

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
