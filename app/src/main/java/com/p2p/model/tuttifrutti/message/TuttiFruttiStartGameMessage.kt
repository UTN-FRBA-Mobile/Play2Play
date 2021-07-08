package com.p2p.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = TuttiFruttiStartGameMessage.TYPE)
data class TuttiFruttiStartGameMessage(val letters: List<Char>, val categories: List<Category>) :
    Message(TYPE) {

    companion object {

        const val TYPE = "tf_start_game"
    }
}
