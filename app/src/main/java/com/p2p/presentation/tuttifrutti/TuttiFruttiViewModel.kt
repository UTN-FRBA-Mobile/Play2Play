package com.p2p.presentation.tuttifrutti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.Category

class TuttiFruttiViewModel(
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
    private val roundsInfo = mutableListOf<RoundInfo>()

    private val lettersByRound: List<Char> by lazy { getRandomLetters() }

    private val _totalRounds = MutableLiveData<Int>()
    val totalRounds: LiveData<Int> = _totalRounds

    private val _selectedCategories = MutableLiveData<List<Category>>()
    val selectedCategories: LiveData<List<Category>> = _selectedCategories


    /**Data for actual round*/
    private val _actualRoundNumber = MutableLiveData<Int>()
    val actualRoundNumer: LiveData<Int> = _actualRoundNumber
    private val _actualRountLetter = MutableLiveData<Char>()
    val actualRoundLetter: LiveData<Char> = _actualRoundLetter


    /**Before playing game*/
    fun setSelectedCategories(categories: List<Category>) { _selectedCategories.value = categories }

    fun setTotalRounds(totalRounds: Int) { _totalRounds.value = totalRounds }

    fun setInitialRoundValues() {
        _actualRound.value = 1
        _actualLetter.value = lettersByRound[0]
    }

    private fun getRandomLetters(): List<Char> =
        availableLetters.map { it }.shuffled().subList(0, totalRounds.value!!)


    /**On Playing game*/
    fun finishRound(categoriesWithValues: Map<Category, String>) {
        if (gameContinues()) {
            generateNextRoundValues()
        }
        roundsInfo.add(RoundInfo(actualLetter.value!!, categoriesWithValues))
        goToReviewOrWait()
    }

    private fun gameContinues(): Boolean {
        val totalRounds: Int = totalRounds.requireValue()
        val actualRound: Int = actualRound.requireValue()
        return actualRound <= totalRounds
    }

    private fun goToReview() {

        //TODO throw when done
        // dispatchSingleTimeEvent(GoToReview)
    }

    private fun generateNextRoundValues() {
        _actualRound.value = actualRound.value?.plus(1)
        _actualLetter.value = lettersByRound[actualRound.value!!.minus(1)]
    }


    companion object {
        //TODO check if we want to delete some letter (e.g: W), if we want all we could put ('A'..'Z')
        const val availableLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    }
}
