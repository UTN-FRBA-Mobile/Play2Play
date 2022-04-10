package ar.com.play2play.model

open class LoadingScreen(val isLoading: Boolean)

class VisibleLoadingScreen(val waitingText: String): LoadingScreen(isLoading = true)

object HiddenLoadingScreen: LoadingScreen(isLoading = false)
