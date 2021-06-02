package com.p2p.model.message

data class ServerHandshakeMessage(val players: List<String>) : Message(TYPE) {

    companion object {

        const val TYPE = "s_handshake"
    }
}
