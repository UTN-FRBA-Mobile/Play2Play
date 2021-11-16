package ar.com.play2play.presentation.truco.lobby.server

import ar.com.play2play.presentation.lobby.ServerLobbyViewModel

class TrucoServerLobbyViewModel : ServerLobbyViewModel() {

    fun updatePlayers(players: List<String>, lobbySize: Int) {
        _isContinueButtonEnabled.value = players.size == lobbySize
    }

}
