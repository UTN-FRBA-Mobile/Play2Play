package ar.com.play2play.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message

@JsonTypeName(value = TuttiFruttiEnoughForMeEnoughForAllMessage.TYPE)
data class TuttiFruttiEnoughForMeEnoughForAllMessage(val player: String) : Message(TYPE) {

    companion object {
        const val TYPE = "tf_enough"
    }
}
