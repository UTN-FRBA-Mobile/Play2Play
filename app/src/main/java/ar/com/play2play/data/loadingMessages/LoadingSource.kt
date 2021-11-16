package ar.com.play2play.data.loadingMessages

import ar.com.play2play.model.LoadingMessageType

/** This interface brings the capacity to show loading messages from message types. */
interface LoadingSource {

    /** Returns the loading text for the give [messageType]. */
    fun getLoadingText(messageType: LoadingMessageType): String

}
