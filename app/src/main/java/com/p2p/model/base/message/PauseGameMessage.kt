package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = PauseGameMessage.TYPE)
data class PauseGameMessage(val lostPlayers: List<String>) : Message(TYPE) {

    companion object {

        const val TYPE = "pause_game"
    }
}
