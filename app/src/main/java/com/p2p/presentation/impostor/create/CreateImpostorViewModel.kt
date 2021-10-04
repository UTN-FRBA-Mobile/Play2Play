package com.p2p.presentation.impostor.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class CreateImpostorViewModel : BaseViewModel<ImpostorCreateEvents>() {

    private var connectedPlayers: List<String>? = null

    /** Whether the start button is enabled or not. */
    private val _startButtonEnabled = MutableLiveData<Boolean>()
    val startButtonEnabled: LiveData<Boolean> = _startButtonEnabled

    fun tryStartGame(keyWord: String) {
        val event = when {
            keyWord.isBlank() -> InvalidInput
            connectedPlayers?.isEmpty() ?: true -> NoConnectedPlayers
            else -> StartGame(keyWord)
        }
        dispatchSingleTimeEvent(event)
    }

    fun updatePlayers(players: List<String>?) {
        connectedPlayers = players
        _startButtonEnabled.value = players!!.count() > 2
    }


}
