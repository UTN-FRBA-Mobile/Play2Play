package com.p2p.presentation.tuttifrutti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.RoundInfo
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

    /**Data for all game*/
    private val roundsInfo = mutableListOf<FinishedRoundInfo>()
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

    private fun getRandomLetters(): List<Char> =
        availableLetters.toList().shuffled().take(totalRounds.requireValue())


    /**On Playing game*/
    fun finishRound(categoriesWithValues: Map<Category, String>) {
        // roundsInfo.add(actualRound.requireValue().finish(categoriesWithValues))
        goToReviewOrWait()
    }

    //TODO this should be called after review
    private fun gameContinues(): Boolean {
        val totalRounds: Int = totalRounds.requireValue()
        val actualRound: Int = actualRound.requireValue().number
        return actualRound <= totalRounds
    }

    private fun goToReviewOrWait() {

        //TODO throw when done
        // dispatchSingleTimeEvent(GoToReview)
    }

    fun generateNextRoundValues() {
        //TODO this should be recieved by the server on the client, and in the server is ok
        //See how to do this logic
        val actualRoundNumber: Int = actualRound.value?.number?.plus(1) ?: 1
        _actualRound.value =
            RoundInfo(lettersByRound[actualRoundNumber.minus(1)], actualRoundNumber)
    }


    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
