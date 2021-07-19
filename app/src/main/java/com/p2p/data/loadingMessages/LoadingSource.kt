package com.p2p.data.loadingMessages

import com.p2p.model.LoadingMessageType

/** This interface brings the capacity to show loading messages from message types. */
interface LoadingSource {

    /** Returns the loading text for the give [messageType]. */
    fun getLoadingText(messageType: LoadingMessageType): String

}
