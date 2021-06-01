package com.p2p.presentation.tuttifrutti.create.rounds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class RoundsNumberViewModel : BaseViewModel<RoundsNumberEvent>() {

    /** The number of rounds to play. */
    private val _roundsNumber = MutableLiveData<Int>()
    val roundsNumber: LiveData<Int> = _roundsNumber

    init {
        _roundsNumber.value = DEFAULT_ROUNDS_NUMBER
    }

    fun increase() {
        _roundsNumber.value = (number() + 1).coerceAtMost(MAXIMUM_ROUND_NUMBER)
    }

    fun decrease() {
        _roundsNumber.value = (number() - 1).coerceAtLeast(1)
    }

    /** Next view to show when Create button is pressed. */
    fun continueCreatingGame() {
        dispatchSingleTimeEvent(GoToTuttiFruttiLobby(roundsNumber.value!!))
    }

    private fun number(): Int = _roundsNumber.value ?: DEFAULT_ROUNDS_NUMBER

    companion object {
        const val MAXIMUM_ROUND_NUMBER = 25
        const val DEFAULT_ROUNDS_NUMBER = 5
    }
}
