package ar.com.play2play.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.model.tuttifrutti.TuttiFruttiFinalScore

@JsonTypeName(value = FinalScoreMessage.TYPE)
data class FinalScoreMessage(val playersScores: List<TuttiFruttiFinalScore>) : Message(TYPE) {

    companion object {

        const val TYPE = "tf_final_score"
    }
}
