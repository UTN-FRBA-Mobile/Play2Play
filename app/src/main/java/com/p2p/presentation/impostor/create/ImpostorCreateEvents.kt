package com.p2p.presentation.impostor.create

/** A base class for all events that could occur on the categories events screen. */
sealed class ImpostorCreateEvents

data class StartGame(val keyWord: String) : ImpostorCreateEvents()

object InvalidInput : ImpostorCreateEvents()

object NoConnectedPlayers : ImpostorCreateEvents()