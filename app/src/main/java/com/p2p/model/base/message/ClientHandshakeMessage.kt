package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = ClientHandshakeMessage.TYPE)
data class ClientHandshakeMessage(val name: String, val joinedGame: String) : Message(TYPE) {

    companion object {

        const val TYPE = "c_handshake"
    }
}
