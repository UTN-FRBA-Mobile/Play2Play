package com.p2p.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.p2p.model.base.message.Message
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = TuttiFruttiClientReviewMessage.TYPE)
data class TuttiFruttiClientReviewMessage(val finishedRoundInfos: Set<FinishedRoundInfo>) : Message(TYPE) {

    companion object {

        const val TYPE = "tf_client_review"
    }
}
