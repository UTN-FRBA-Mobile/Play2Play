package ar.com.play2play.model.impostor.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message

@JsonTypeName(value = ImpostorEndGame.TYPE)
class ImpostorEndGame : Message(TYPE) {

    companion object {
        const val TYPE = "im_end"
    }
}
