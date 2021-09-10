package com.p2p.presentation.truco.create

import com.p2p.presentation.base.BaseViewModel

class CreateTrucoViewModel : BaseViewModel<TrucoCreateEvents>() {

    private var connectedPlayers: List<String>? = null

    fun tryStartGame(keyWord: String) {
        val event = when {
            connectedPlayers?.isEmpty() ?: true -> NoConnectedPlayers
            else -> StartGame(keyWord)
        }
        dispatchSingleTimeEvent(event)
    }

    fun updatePlayers(players: List<String>?) {
        connectedPlayers = players
    }


}