package com.p2p.data.loadingMessages

import com.p2p.model.LoadingMessageType

data class LoadingTextRepository(private val loadingSource: LoadingSource) {

    fun getText(messageType: LoadingMessageType) = loadingSource.getLoadingText(messageType)
}
