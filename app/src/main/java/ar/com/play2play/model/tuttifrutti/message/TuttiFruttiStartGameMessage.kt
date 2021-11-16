package ar.com.play2play.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = TuttiFruttiStartGameMessage.TYPE)
data class TuttiFruttiStartGameMessage(val letters: List<Char>, val categories: List<Category>) :
    Message(TYPE) {

    companion object {

        const val TYPE = "tf_start_game"
    }
}
