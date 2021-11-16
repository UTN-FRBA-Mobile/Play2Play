package ar.com.play2play.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = TuttiFruttiSendWordsMessage.TYPE)
data class TuttiFruttiSendWordsMessage(val words: LinkedHashMap<Category, String>) : Message(TYPE) {

    companion object {

        const val TYPE = "tf_send_words"
    }
}
