package com.p2p.presentation.tuttifrutti

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.HiddenLoadingScreen
import com.p2p.model.LoadingScreen
import com.p2p.model.VisibleLoadingScreen
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.Category

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

    //Loading value for loading screen, being first if isLoading and second the text to show
    protected val _loadingScreen = MutableLiveData<LoadingScreen>()
    val loadingScreen: LiveData<LoadingScreen> = _loadingScreen

    protected val _totalRounds = MutableLiveData<Int>()
    val totalRounds: LiveData<Int> = _totalRounds

    protected val _finishedRoundInfos = MutableLiveData(listOf<FinishedRoundInfo>())
    val finishedRoundInfos: LiveData<List<FinishedRoundInfo>> = _finishedRoundInfos

    private val _finishedRoundsPointsInfos = MutableLiveData(listOf<FinishedRoundPointsInfo>())
    val finishedRoundsPointsInfos: LiveData<List<FinishedRoundPointsInfo>> =
        _finishedRoundsPointsInfos

    private val _categoriesToPlay = MutableLiveData<List<Category>>()
    val categoriesToPlay: LiveData<List<Category>> = _categoriesToPlay

    private val _actualRound = MutableLiveData<RoundInfo>()
    val actualRound: LiveData<RoundInfo> = _actualRound

    init {
        _loadingScreen.value = HiddenLoadingScreen
    }

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
            dispatchSingleTimeEvent(GoToFinalScore)
        } else {
            startRound()
        }
    }

    fun generateNextRoundValues() {
        val round = (actualRound.value?.number ?: 0) + 1
        _actualRound.value = RoundInfo(lettersByRound[round - 1], round)
    }

    abstract fun startGame()

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

    protected fun stopRound() {
        dispatchSingleTimeEvent(ObtainWords)
    }

    protected fun startLoading(loadingMessage: String) {
        _loadingScreen.value = VisibleLoadingScreen(loadingMessage)
    }

    fun stopLoading() {
        _loadingScreen.value = HiddenLoadingScreen
    }

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
