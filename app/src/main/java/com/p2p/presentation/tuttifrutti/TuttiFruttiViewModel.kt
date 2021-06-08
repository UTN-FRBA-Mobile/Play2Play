package com.p2p.presentation.tuttifrutti

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.MessageReceived
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
    instructionsRepository: InstructionsRepository
) : GameViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    Game.TUTTI_FRUTTI
) {

    private val lettersByRound: List<Char> by lazy { getRandomLetters() }

    protected val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

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

    fun generateNextRoundValues() {
        //TODO this should be recieved by the server on the client, and in the server is ok
        //See how to do this logic
        val actualRoundNumber: Int = actualRound.value?.number?.plus(1) ?: 1
        _actualRound.value =
            RoundInfo(lettersByRound[actualRoundNumber.minus(1)], actualRoundNumber)
    }

    abstract fun sendWords(categoriesWords: Map<Category, String>)

    @CallSuper
    open fun enoughForMeEnoughForAll() {
        _isLoading.value = true
        connection.write(TuttiFruttiEnoughForMeEnoughForAllMessage())
        dispatchSingleTimeEvent(ObtainWords)
    }

    @CallSuper
    override fun receiveMessage(messageReceived: MessageReceived) {
        super.receiveMessage(messageReceived)
        when (messageReceived.message) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> stopRound(messageReceived)
        }
    }

    @CallSuper
    protected open fun stopRound(messageReceived: MessageReceived) {
        _isLoading.value = true
        dispatchSingleTimeEvent(ObtainWords)
    }

    private fun getRandomLetters() = availableLetters.toList().shuffled().take(totalRounds.requireValue())

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
