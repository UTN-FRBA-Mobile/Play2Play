package ar.com.play2play.model.tuttifrutti.message

import com.fasterxml.jackson.annotation.JsonTypeName
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.model.tuttifrutti.FinishedRoundInfo
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

@JsonTypeName(value = TuttiFruttiClientReviewMessage.TYPE)
data class TuttiFruttiClientReviewMessage(val finishedRoundInfos: Set<FinishedRoundInfo>) : Message(TYPE) {

    companion object {

        const val TYPE = "tf_client_review"
    }
}
