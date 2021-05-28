package com.p2p.presentation.base.game

sealed class GameEvent

object GoToServerLobby : GameEvent()
object GoToClientLobby : GameEvent()

open class SpecificGameEvent : GameEvent()