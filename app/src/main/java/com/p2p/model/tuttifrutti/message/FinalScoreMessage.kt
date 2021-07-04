package com.p2p.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.presentation.tuttifrutti.finalscore.TuttiFruttiFinalScore

@JsonTypeName(value = FinalScoreMessage.TYPE)
data class FinalScoreMessage(val playersScores: List<TuttiFruttiFinalScore>) : Message(TYPE) {

    companion object {

        const val TYPE = "tf_final_score"
    }
}
