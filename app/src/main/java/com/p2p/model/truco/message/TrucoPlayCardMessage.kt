package com.p2p.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.presentation.truco.PlayedCard

@JsonTypeName(value = TrucoActionMessage.TYPE)
data class TrucoPlayCardMessage(val playedCard: PlayedCard) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_play_card"
    }
}

