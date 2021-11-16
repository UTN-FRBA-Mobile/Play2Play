package ar.com.play2play.presentation.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.presentation.base.BaseViewModel

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