package com.p2p.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.model.truco.TeamPlayer

// TODO: Add the rest of the needed attributes to the start game message
@JsonTypeName(value = TrucoStartGameMessage.TYPE)
data class TrucoStartGameMessage(val teamPlayers: List<TeamPlayer>, val totalPlayers: Int, val totalPoints: Int) :
    Message(TYPE) {

    companion object {
        const val TYPE = "tr_start_game"
    }
}
