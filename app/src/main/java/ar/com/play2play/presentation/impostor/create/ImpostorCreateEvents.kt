package ar.com.play2play.presentation.impostor.create

/** A base class for all events that could occur on the categories events screen. */
sealed class ImpostorCreateEvents

data class StartGame(val keyWord: String, val keyWordTheme: String) : ImpostorCreateEvents()

object InvalidKeyWordInput : ImpostorCreateEvents()

object InvalidKeyWordThemeInput : ImpostorCreateEvents()

object NotEnoughPlayers : ImpostorCreateEvents()
