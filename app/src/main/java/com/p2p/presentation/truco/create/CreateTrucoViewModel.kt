package com.p2p.presentation.truco.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class CreateTrucoViewModel:
    BaseViewModel<CreateTrucoEvents>() {

    private val _totalPlayers = MutableLiveData<Int>()
    val totalPlayers: LiveData<Int> = _totalPlayers


    fun createLobby(players: Int) {
        dispatchSingleTimeEvent(CreateTrucoLobbyEvent(players))
    }
}