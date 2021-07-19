package com.p2p.data.loadingMessages

/** This interface brings the capacity to show loading messages from message types. */
interface LoadingSource {

    /** Returns the loading text for the give [messageType]. */
    fun getLoadingText(messageType: MessageType): String

    enum class MessageType {
        TF_WAITING_FOR_REVIEW,
        TF_WAITING_FOR_WORDS
    }
}
