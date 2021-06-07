package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = ServerHandshakeMessage.TYPE)
data class ServerHandshakeMessage(val players: List<String>) : Message(TYPE) {

    companion object {

        const val TYPE = "s_handshake"
    }
}
