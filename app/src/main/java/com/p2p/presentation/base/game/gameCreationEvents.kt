package com.p2p.presentation.base.game

interface AbstractGameCreationEvent

sealed class GameCreationEvent : AbstractGameCreationEvent

object GoToServerLobby : GameCreationEvent()
object GoToClientLobby : GameCreationEvent()