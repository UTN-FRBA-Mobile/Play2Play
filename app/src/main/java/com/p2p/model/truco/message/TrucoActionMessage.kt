package com.p2p.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.model.truco.PlayerTeam
import com.p2p.presentation.truco.actions.TrucoAction

@JsonTypeName(value = TrucoActionMessage.TYPE)
data class TrucoActionMessage(val action: TrucoAction, val playerTeam: PlayerTeam) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_action"
    }
}
