package com.p2p.presentation.impostor.create

import com.p2p.presentation.base.BaseViewModel

class CreateImpostorViewModel : BaseViewModel<ImpostorCreateEvents>() {

    private var connectedPlayers: List<String>? = null

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
    }


}
