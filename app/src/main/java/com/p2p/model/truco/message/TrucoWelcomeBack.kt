package com.p2p.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.model.truco.TeamPlayer

@JsonTypeName(value = TrucoWelcomeBack.TYPE)
data class TrucoWelcomeBack(
    val teamPlayers: List<TeamPlayer>,
    val totalPlayers: Int,
    val totalPoints: Int,
    val scores: Map<Int, Int>,
    val handPlayer: TeamPlayer
) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_welcome_back"
    }
}
