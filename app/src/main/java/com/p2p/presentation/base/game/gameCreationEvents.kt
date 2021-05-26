package com.p2p.presentation.tuttifrutti

interface AbstractGameCreationEvent

sealed class GameCreationEvent : AbstractGameCreationEvent

object GoToServerLobby : GameCreationEvent()
object GoToClientLobby : GameCreationEvent()