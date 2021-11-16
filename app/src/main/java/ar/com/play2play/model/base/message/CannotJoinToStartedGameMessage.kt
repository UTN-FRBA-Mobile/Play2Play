package ar.com.play2play.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = CannotJoinToStartedGameMessage.TYPE)
class CannotJoinToStartedGameMessage : Message(TYPE) {

    companion object {

        const val TYPE = "cannot_join_to_started_game"
    }
}
