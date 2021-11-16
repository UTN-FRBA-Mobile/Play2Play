package ar.com.play2play.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = RoomIsAlreadyFullMessage.TYPE)
class RoomIsAlreadyFullMessage : Message(TYPE) {

    companion object {

        const val TYPE = "room_is_already_full"
    }
}
