package com.p2p.presentation.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class DefaultServerLobbyViewModel: ServerLobbyViewModel() {
    fun updatePlayers(players: List<String>) {
        _isContinueButtonEnabled.value = players.size >= LOBBY_MIN_SIZE
    }

    companion object {
        const val LOBBY_MIN_SIZE = 2
    }
}

abstract class ServerLobbyViewModel: BaseViewModel<LobbyEvent>(){
    protected val _isContinueButtonEnabled = MutableLiveData(false)
    val isContinueButtonEnabled: LiveData<Boolean> = _isContinueButtonEnabled
}