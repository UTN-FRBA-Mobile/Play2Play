package com.p2p.model.impostor.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message

@JsonTypeName(value = ImpostorEndGame.TYPE)
class ImpostorEndGame : Message(TYPE) {

    companion object {
        const val TYPE = "im_end"
    }
}
