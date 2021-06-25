package com.p2p.model

data class Loading(val isLoading: Boolean, val loadingText: String) {
    fun startLoading(text: String = loadingText) = Loading(isLoading = true, loadingText = text)
    fun stopLoading() = this.copy(isLoading = false)
}
