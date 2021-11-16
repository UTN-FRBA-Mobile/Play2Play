package ar.com.play2play.model.base.message

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName(value = RejoinNameErrorMessage.TYPE)
data class RejoinNameErrorMessage(val availableNames: List<String>) : Message(TYPE) {

    companion object {

        const val TYPE = "rejoin_name_error"
    }
}
