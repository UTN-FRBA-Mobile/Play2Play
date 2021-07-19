package com.p2p.data.loadingMessages

data class LoadingTextRepository(private val loadingSource: LoadingSource) {

    fun getText(messageType: LoadingSource.MessageType) = loadingSource.getLoadingText(messageType)
}
