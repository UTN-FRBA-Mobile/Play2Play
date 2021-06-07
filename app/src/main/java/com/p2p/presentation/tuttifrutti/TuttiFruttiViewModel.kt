package com.p2p.presentation.tuttifrutti

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.MessageReceived
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.Category

open class TuttiFruttiViewModel(
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

    private val behaviour = if (isServer()) ServerTuttiFruttiBehaviour() else ClientTuttiFruttiBehaviour()
    private val lettersByRound: List<Char> by lazy { getRandomLetters() }

    private val _totalRounds = MutableLiveData<Int>()
    val totalRounds: LiveData<Int> = _totalRounds

    private val _selectedCategories = MutableLiveData<List<Category>>()
    val selectedCategories: LiveData<List<Category>> = _selectedCategories

    /** Data for the actual round. */
    private val _actualRound = MutableLiveData<RoundInfo>()
    val actualRound: LiveData<RoundInfo> = _actualRound

    /** Set the categories selected by the user when creating the game. */
    fun setSelectedCategories(categories: List<Category>) {
        _selectedCategories.value = categories
    }

    fun setTotalRounds(totalRounds: Int) {
        _totalRounds.value = totalRounds
    }

    fun enoughForMeEnoughForAll() = behaviour.enoughForMeEnoughForAll()

    fun sendWords(categoriesWords: Map<Category, String>) = behaviour.sendWords(categoriesWords)

    fun generateNextRoundValues() {
        //TODO this should be recieved by the server on the client, and in the server is ok
        //See how to do this logic
        val actualRoundNumber: Int = actualRound.value?.number?.plus(1) ?: 1
        _actualRound.value =
            RoundInfo(lettersByRound[actualRoundNumber.minus(1)], actualRoundNumber)
    }

    override fun receiveMessage(messageReceived: MessageReceived) {
        super.receiveMessage(messageReceived)
        when (val message = messageReceived.message) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> behaviour.stopRound(messageReceived)
            is TuttiFruttiSendWordsMessage -> behaviour.acceptWords(messageReceived, message.words)
        }
    }

    private fun showLoading() {
        // TODO: show overlay loading
    }

    private fun getRandomLetters() = availableLetters.toList().shuffled().take(totalRounds.requireValue())

    private inner class ServerTuttiFruttiBehaviour : TuttiFruttiBehaviour() {

        private var categoriesWordsPerPlayer = mutableMapOf<Long, Map<Category, String>>()

        override fun sendWords(categoriesWords: Map<Category, String>) {
            super.sendWords(categoriesWords)
            categoriesWordsPerPlayer[MYSELF_ID] = categoriesWords
            goToReviewIfCorresponds()
        }

        override fun acceptWords(messageReceived: MessageReceived, categoriesWords: Map<Category, String>) {
            super.acceptWords(messageReceived, categoriesWords)
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
            }
        }
    }

    private inner class ClientTuttiFruttiBehaviour : TuttiFruttiBehaviour() {

        private var stopRoundMessageReceived: MessageReceived? = null

        override fun stopRound(messageReceived: MessageReceived) {
            super.stopRound(messageReceived)
            stopRoundMessageReceived = messageReceived
        }

        override fun sendWords(categoriesWords: Map<Category, String>) {
            super.sendWords(categoriesWords)
            stopRoundMessageReceived?.let { connection.answer(it, TuttiFruttiSendWordsMessage(categoriesWords)) }
        }
    }

    private abstract inner class TuttiFruttiBehaviour {

        @CallSuper
        open fun enoughForMeEnoughForAll() {
            // TODO: why it's not working the first time? (checked from the server, check from the client and with more than two connected devices)
            showLoading()
            connection.write(TuttiFruttiEnoughForMeEnoughForAllMessage())
            dispatchSingleTimeEvent(ObtainWords)
        }

        @CallSuper
        open fun stopRound(messageReceived: MessageReceived) {
            showLoading()
            dispatchSingleTimeEvent(ObtainWords)
        }

        @CallSuper
        open fun sendWords(categoriesWords: Map<Category, String>) {
        }

        @CallSuper
        open fun acceptWords(messageReceived: MessageReceived, categoriesWords: Map<Category, String>) {
        }
    }

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
