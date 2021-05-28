package com.p2p.model.message

data class ClientHandshakeMessage(val name: String) : Message(TYPE) {

    companion object {

        const val TYPE = "c_handshake"
    }
}
