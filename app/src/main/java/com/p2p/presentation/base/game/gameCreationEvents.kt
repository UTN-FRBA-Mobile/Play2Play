package com.p2p.presentation.base.game


sealed class GameCreationEvent

object GoToServerLobby : GameCreationEvent()
object GoToClientLobby : GameCreationEvent()

open class SpecificCreationEvents : GameCreationEvent()