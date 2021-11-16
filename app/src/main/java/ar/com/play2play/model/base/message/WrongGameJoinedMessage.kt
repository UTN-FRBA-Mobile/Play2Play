package ar.com.play2play.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = WrongGameJoinedMessage.TYPE)
class WrongGameJoinedMessage : Message(TYPE) {

    companion object {

        const val TYPE = "wrong_game_joined"
    }
}
