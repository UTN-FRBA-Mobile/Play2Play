package ar.com.play2play.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.model.truco.TeamPlayer

@JsonTypeName(value = TrucoStartGameMessage.TYPE)
data class TrucoStartGameMessage(val teamPlayers: List<TeamPlayer>, val totalPlayers: Int, val totalPoints: Int) :
    Message(TYPE) {

    companion object {
        const val TYPE = "tr_start_game"
    }
}
