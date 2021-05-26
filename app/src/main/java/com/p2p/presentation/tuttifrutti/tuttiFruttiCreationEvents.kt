package com.p2p.presentation.tuttifrutti

sealed class TuttiFruttiCreationEvent

object GoToSelectCategories : TuttiFruttiCreationEvent()
object GoToServerLobby : TuttiFruttiCreationEvent()
object GoToClientLobby : TuttiFruttiCreationEvent()