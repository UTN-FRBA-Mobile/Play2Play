package com.p2p.presentation.truco.create

import com.p2p.presentation.base.BaseViewModel

class CreateTrucoViewModel:
    BaseViewModel<CreateTrucoEvents>() {

    fun createLobby(players: Int) {
        dispatchSingleTimeEvent(CreateTrucoLobbyEvent(players))
    }
}