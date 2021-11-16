package ar.com.play2play.model.impostor.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message

@JsonTypeName(value = ImpostorAssignWord.TYPE)
class ImpostorAssignWord(val word: String, val wordTheme: String, val impostor: String) : Message(TYPE) {

    companion object {
        const val TYPE = "im_assign"
    }
}
