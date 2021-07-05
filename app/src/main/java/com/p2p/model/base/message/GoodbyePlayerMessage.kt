package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = GoodbyePlayerMessage.TYPE)
data class GoodbyePlayerMessage(val name: String) : Message(TYPE) {

    companion object {

        const val TYPE = "goodbye"
    }
}
