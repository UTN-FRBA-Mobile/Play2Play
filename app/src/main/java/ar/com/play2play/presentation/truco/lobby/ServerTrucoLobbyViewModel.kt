package ar.com.play2play.presentation.truco.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.presentation.base.BaseViewModel

class ServerTrucoLobbyViewModel: BaseViewModel<LobbyEvent>() {

    private val _isContinueButtonEnabled = MutableLiveData(false)
    val isContinueButtonEnabled: LiveData<Boolean> = _isContinueButtonEnabled

    private var players: List<String>? = null
    private var totalPlayers : Int? = null

    fun setPlayers(players: List<String>) {
        this.players = players
        totalPlayers?.let { updateContinueButtonAvailability(players, it) }
    }

    fun setTotalPlayers(totalPlayers: Int) {
        this.totalPlayers = totalPlayers
        players?.let { updateContinueButtonAvailability(it, totalPlayers) }
    }

    private fun updateContinueButtonAvailability(players: List<String>, totalPlayers: Int) {
        _isContinueButtonEnabled.value = players.size == totalPlayers
    }
}
