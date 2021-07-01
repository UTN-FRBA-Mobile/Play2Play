package com.p2p.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message

@JsonTypeName(value = TuttiFruttiStartRoundMessage.TYPE)
class TuttiFruttiStartRoundMessage : Message(TYPE) {

    companion object {

        const val TYPE = "tf_start_round"
    }
}