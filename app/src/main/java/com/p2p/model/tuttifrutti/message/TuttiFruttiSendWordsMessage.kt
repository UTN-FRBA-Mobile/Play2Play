package com.p2p.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = TuttiFruttiSendWordsMessage.TYPE)
data class TuttiFruttiSendWordsMessage(val words: Map<Category, String>) : Message(TYPE) {

    companion object {

        const val TYPE = "tf_send_words"
    }
}