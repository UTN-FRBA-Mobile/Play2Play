package com.p2p.model.impostor.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message

@JsonTypeName(value = ImpostorAssignWord.TYPE)
class ImpostorAssignWord(val word: String, val impostor: String) : Message(TYPE) {

    companion object {
        const val TYPE = "im_assign"
    }
}
