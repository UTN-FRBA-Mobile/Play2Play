package com.p2p.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.model.truco.Card
import com.p2p.model.truco.PlayerWithCards

@JsonTypeName(value = TrucoCardsMessage.TYPE)
data class TrucoCardsMessage(val cardsForPlayers: List<PlayerWithCards>) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_cards"
    }
}
