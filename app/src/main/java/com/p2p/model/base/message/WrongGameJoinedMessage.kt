package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = WrongGameJoinedMessage.TYPE)
class WrongGameJoinedMessage : Message(TYPE) {

    companion object {

        const val TYPE = "wrong_game_joined"
    }
}
