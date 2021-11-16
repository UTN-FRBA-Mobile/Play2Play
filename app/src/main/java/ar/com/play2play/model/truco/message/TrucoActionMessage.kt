package ar.com.play2play.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.model.truco.TeamPlayer
import ar.com.play2play.presentation.truco.actions.TrucoAction

@JsonTypeName(value = TrucoActionMessage.TYPE)
data class TrucoActionMessage(val action: TrucoAction, val teamPlayer: TeamPlayer) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_action"
    }
}