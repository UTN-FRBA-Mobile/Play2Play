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

    /** Data for all game */
    private val roundsInfo = mutableListOf<FinishedRoundInfo>()
    protected lateinit var lettersByRound: List<Char>

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

    fun startRound() {
        generateNextRoundValues()
    }

    // TODO: this should be called from the server lobby when startGame button is clicked.
    abstract fun startGame()

    /** On Playing game */
    fun finishRound(categoriesWithValues: Map<Category, String>) {
        // roundsInfo.add(actualRound.requireValue().finish(categoriesWithValues))
        goToReviewOrWait()
    }

    private fun generateNextRoundValues() {
        val actualRoundNumber: Int = actualRound.value?.number?.plus(1) ?: 1
        _actualRound.value =
            RoundInfo(lettersByRound[actualRoundNumber.minus(1)], actualRoundNumber)
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

    companion object {
        const val availableLetters = "ABCDEFGHIJKLMNOPRSTUVY"
    }
}
