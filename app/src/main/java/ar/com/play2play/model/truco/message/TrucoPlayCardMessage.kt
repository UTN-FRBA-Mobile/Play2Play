package ar.com.play2play.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.presentation.truco.PlayedCard

@JsonTypeName(value = TrucoPlayCardMessage.TYPE)
data class TrucoPlayCardMessage(val playedCard: PlayedCard) : Message(TYPE) {

    companion object {
        const val TYPE = "tr_play_card"
    }
}
