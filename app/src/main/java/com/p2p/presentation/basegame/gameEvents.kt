package com.p2p.presentation.basegame

import com.p2p.model.GameInfo

sealed class GameEvent

object GoToCreate : GameEvent()

object GoToServerLobby : GameEvent()

object GoToClientLobby : GameEvent()

data class GoToPlay(val gameInfo: GameInfo) : GameEvent()

data class OpenInstructions(val instructions: String) : GameEvent()

open class SpecificGameEvent : GameEvent()