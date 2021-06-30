package com.p2p.data.loadingMessages
import com.p2p.model.base.message.Message

/** This interface brings the capacity to show loading messages from message. */
interface LoadingSource {

    /** Returns the loading text while the [message] is being send . */
    fun getLoadingText(messageType: String): String
}
