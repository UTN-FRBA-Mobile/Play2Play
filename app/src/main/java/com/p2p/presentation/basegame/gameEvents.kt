package com.p2p.presentation.basegame

sealed class GameEvent

object GoToServerLobby : GameEvent()

object GoToClientLobby : GameEvent()

data class OpenInstructions(val instructions: String) : GameEvent()

open class SpecificGameEvent : GameEvent()