package ar.com.play2play.data.loadingMessages

import ar.com.play2play.model.LoadingMessageType

data class LoadingTextRepository(private val loadingSource: LoadingSource) {

    fun getText(messageType: LoadingMessageType) = loadingSource.getLoadingText(messageType)
}
