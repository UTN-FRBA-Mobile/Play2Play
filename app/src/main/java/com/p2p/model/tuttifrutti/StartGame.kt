package com.p2p.model.tuttifrutti

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = StartGame.TYPE)
data class StartGame(val letters: List<Char>, val categories: List<Category>) : Message(TYPE) {

    companion object {

        const val TYPE = "start_game"
    }
}

