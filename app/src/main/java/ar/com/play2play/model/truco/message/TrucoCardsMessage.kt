package ar.com.play2play.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.model.truco.Card
import ar.com.play2play.model.truco.PlayerWithCards

@JsonTypeName(value = TrucoCardsMessage.TYPE)
data class TrucoCardsMessage(val cardsForPlayers: List<PlayerWithCards>) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_cards"
    }
}
