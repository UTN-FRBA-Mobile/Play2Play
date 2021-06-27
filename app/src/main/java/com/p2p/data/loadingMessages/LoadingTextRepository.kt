package com.p2p.data.loadingMessages

import com.p2p.model.base.message.Message

data class LoadingTextRepository(private val loadingSource: LoadingSource) {

    fun getText(messageType: String): String = loadingSource.getLoadingText(messageType)
}