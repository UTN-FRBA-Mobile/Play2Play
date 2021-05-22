package com.p2p.presentation.tuttifrutti.create.rounds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.home.games.GamesEvents
import com.p2p.presentation.home.games.GoToTuttiFruttiLobby

class RoundsNumberViewModel : BaseViewModel<GamesEvents>() {

    /** The number of rounds to play. */
    private val _roundsNumber = MutableLiveData<Int>()
    val roundsNumber: LiveData<Int> = _roundsNumber

    fun increase(number: Int) {
        if (number < MAXIMUM_ROUND_NUMBER)
            _roundsNumber.value = number + 1
    }

    fun decrease(number: Int) {
        if (number > 1)
            _roundsNumber.value = number - 1
    }

    /** Next view to show when Continue button is pressed. */
    fun continueCreatingGame() {
        dispatchSingleTimeEvent(GoToTuttiFruttiLobby)
    }

    companion object {
        const val MAXIMUM_ROUND_NUMBER = 25
    }
}