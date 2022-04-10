package ar.com.play2play.presentation.tuttifrutti

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.LoadingMessageType
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.tuttifrutti.FinishedRoundInfo
import ar.com.play2play.model.tuttifrutti.FinishedRoundPointsInfo
import ar.com.play2play.model.tuttifrutti.RoundInfo
import ar.com.play2play.model.tuttifrutti.TuttiFruttiFinalScore
import ar.com.play2play.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import ar.com.play2play.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import ar.com.play2play.presentation.basegame.ConnectionType
import ar.com.play2play.presentation.basegame.GameViewModel
import ar.com.play2play.presentation.basegame.KillGame
import ar.com.play2play.presentation.extensions.requireValue
import ar.com.play2play.presentation.home.games.Game
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

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

    private val _totalRounds = MutableLiveData<Int>()
    val totalRounds: LiveData<Int> = _totalRounds

    private val _finishedRoundInfos = MutableLiveData(setOf<FinishedRoundInfo>())
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

    private var saidEnoughPlayer: String? = null

    abstract fun startRound()

    abstract fun goToReview()

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

    fun setFinalScores(scores: List<TuttiFruttiFinalScore>) {
        _finalScores.value = scores
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

    /** Enough for me enough for all will say to the room that the round is finished. */
    fun enoughForMeEnoughForAll() {
        connection.write(TuttiFruttiEnoughForMeEnoughForAllMessage(userName))
        saidEnough(userName)
    }

    fun sendWords(categoriesWords: LinkedHashMap<Category, String>) {
        connection.write(TuttiFruttiSendWordsMessage(userName, categoriesWords))
        acceptWords(MYSELF_PEER_ID, userName, categoriesWords)
    }

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

    override fun goToPlay() {
        gameAlreadyStarted = true
        super.goToPlay()
    }

    @CallSuper
    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> saidEnough(message.player)
            is TuttiFruttiSendWordsMessage -> acceptWords(conversation.peer, message.player, message.words)
        }
    }

    override fun onClientConnectionLost(peerId: Long) {
        super.onClientConnectionLost(peerId)
        if (gameAlreadyStarted && connectedPlayers.size == 1) {
            dispatchErrorScreen(SinglePlayerOnGame {
                dispatchSingleTimeEvent(KillGame)
            })
        }
    }

    protected fun goToFinalScore() {
        gameAlreadyFinished = true
        dispatchSingleTimeEvent(GoToFinalScore)
    }

    @CallSuper
    protected open fun saidEnough(player: String) {
        startLoading(loadingTextRepository.getText(LoadingMessageType.TF_WAITING_FOR_WORDS))
        saidEnoughPlayer = player
        _finishedRoundInfos.value = emptySet()
        stopRound()
    }

    @CallSuper
    protected open fun goToReviewIfCorresponds() {
        if (finishedRoundInfos.requireValue().size == connectedPlayers.size) {
            // When all the players send their words, go to the review and clean the players round words.
            goToReview()
        }
    }

    private fun stopRound() = dispatchSingleTimeEvent(ObtainWords)

    private fun acceptWords(peer: Long, player: String, categoriesWords: LinkedHashMap<Category, String>) {
        _finishedRoundInfos.value = _finishedRoundInfos.requireValue() + FinishedRoundInfo(
            peer = peer,
            player = player,
            categoriesWords = categoriesWords,
            saidEnough = player == saidEnoughPlayer
        )
        goToReviewIfCorresponds()
    }

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
