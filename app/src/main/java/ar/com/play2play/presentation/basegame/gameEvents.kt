package ar.com.play2play.presentation.basegame

sealed class GameEvent

object GoToCreate : GameEvent()

object GoToServerLobby : GameEvent()

object GoToClientLobby : GameEvent()

object GoToPlay : GameEvent()

object KillGame : GameEvent()

object ResumeGame : GameEvent()

data class PauseGame(val lostPlayers: List<String>) : GameEvent()

data class OpenInstructions(val instructions: String) : GameEvent()

open class SpecificGameEvent : GameEvent()