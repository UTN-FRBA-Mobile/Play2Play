package com.p2p.presentation.tuttifrutti.create.rounds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class RoundsNumberViewModel : BaseViewModel<RoundsNumberEvents>() {

    /** The number of rounds to play. */
    private val _roundsNumber = MutableLiveData<Int>()
    val roundsNumber: LiveData<Int> = _roundsNumber

    init {
        _roundsNumber.value = DEFAULT_ROUNDS_NUMBER
    }

    fun increase() {
        if (number() < MAXIMUM_ROUND_NUMBER)
            _roundsNumber.value = number() + 1
    }

    fun decrease() {
        if (number() > 1)
            _roundsNumber.value = number() - 1
    }

    /** Next view to show when Create button is pressed. */
    fun continueCreatingGame() {
        dispatchSingleTimeEvent(GoToTuttiFruttiLobby)
    }

    private fun number(): Int = _roundsNumber.value ?: DEFAULT_ROUNDS_NUMBER

    companion object {
        const val MAXIMUM_ROUND_NUMBER = 25
        const val DEFAULT_ROUNDS_NUMBER = 5
    }
}