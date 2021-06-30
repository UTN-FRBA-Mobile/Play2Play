package com.p2p.model

open class LoadingScreen(val isLoading: Boolean)

class VisibleLoadingScreen(val waitingText: String): LoadingScreen(isLoading = true)

object HiddenLoadingScreen: LoadingScreen(isLoading = false)
