package com.p2p.model.truco.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.presentation.truco.finalscore.TrucoFinalScore

@JsonTypeName(value = FinalScoreMessage.TYPE)
data class FinalScoreMessage(val playersScores: List<TrucoFinalScore>) : Message(TYPE) {

    companion object {

        const val TYPE = "tr_final_score"
    }
}
