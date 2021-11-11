package com.p2p.presentation.truco.lobby.server

import com.p2p.presentation.lobby.ServerLobbyViewModel

class TrucoServerLobbyViewModel : ServerLobbyViewModel() {

    fun updatePlayers(players: List<String>, lobbySize: Int) {
        _isContinueButtonEnabled.value = players.size == lobbySize
    }

}
