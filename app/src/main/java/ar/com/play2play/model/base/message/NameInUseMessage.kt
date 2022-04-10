package ar.com.play2play.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = NameInUseMessage.TYPE)
class NameInUseMessage : Message(TYPE) {

    companion object {

        const val TYPE = "name_in_use"
    }
}
