package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = NameInUseMessage.TYPE)
class NameInUseMessage : Message(TYPE) {

    companion object {

        const val TYPE = "name_in_use"
    }
}
