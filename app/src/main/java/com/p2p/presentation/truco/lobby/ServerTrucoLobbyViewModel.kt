package com.p2p.presentation.truco.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.presentation.base.BaseViewModel

class ServerTrucoLobbyViewModel: BaseViewModel<LobbyEvent>() {
    private val _isContinueButtonEnabled = MutableLiveData(false)
    val isContinueButtonEnabled: LiveData<Boolean> = _isContinueButtonEnabled

    private var players: List<String>? = null
    private var totalPlayers : Int? = null

    fun setPlayers(setPlayers: List<String>) {
        players = setPlayers
        totalPlayers?.let { updatePlayers(setPlayers, it) }
    }

    fun setTotalPlayers(setTotalPlayers: Int) {
        totalPlayers = setTotalPlayers
        players?.let { updatePlayers(it, setTotalPlayers) }
    }

    fun updatePlayers(players: List<String>, totalPlayers: Int) {
        _isContinueButtonEnabled.value = players.size >= totalPlayers
    }
}
