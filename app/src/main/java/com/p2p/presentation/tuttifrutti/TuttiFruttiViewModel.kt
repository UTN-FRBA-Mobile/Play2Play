package com.p2p.presentation.tuttifrutti

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.ConversationMessage
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.Category

abstract class TuttiFruttiViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository
) : GameViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    Game.TUTTI_FRUTTI
) {

    protected lateinit var lettersByRound: List<Char>

    protected val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    protected val _totalRounds = MutableLiveData<Int>()
    val totalRounds: LiveData<Int> = _totalRounds

    private val _categoriesToPlay = MutableLiveData<List<Category>>()
    val categoriesToPlay: LiveData<List<Category>> = _categoriesToPlay

    /** Data for the actual round. */
    private val _actualRound = MutableLiveData<RoundInfo>()
    val actualRound: LiveData<RoundInfo> = _actualRound

    /** Set the categories selected by the user when creating the game . */
    fun setCategoriesToPlay(categories: List<Category>) {
        _categoriesToPlay.value = categories
    }

    fun setTotalRounds(totalRounds: Int) {
        _totalRounds.value = totalRounds
    }

    fun generateNextRoundValues() {
        //TODO this should be recieved by the server on the client, and in the server is ok
        //See how to do this logic
        val actualRoundNumber: Int = actualRound.value?.number?.plus(1) ?: 1
        _actualRound.value =
            RoundInfo(lettersByRound[actualRoundNumber.minus(1)], actualRoundNumber)
    }

    fun startRound() {
        generateNextRoundValues()
    }

    // TODO: this should be called from the server lobby when startGame button is clicked.
    abstract fun startGame()

    fun enoughForMeEnoughForAll() {
        _isLoading.value = true
        connection.write(TuttiFruttiEnoughForMeEnoughForAllMessage())
    }

    abstract fun sendWords(categoriesWords: Map<Category, String>)

    @CallSuper
    override fun receiveMessage(conversationMessage: ConversationMessage) {
        super.receiveMessage(conversationMessage)
        when (conversationMessage.message) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> onReceiveEnoughForAll(conversationMessage)
        }
    }

    protected open fun onReceiveEnoughForAll(conversationMessage: ConversationMessage) = stopRound()

    private fun stopRound() {
        _isLoading.value = true
        dispatchSingleTimeEvent(ObtainWords)
    }

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
