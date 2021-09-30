package com.p2p.presentation.truco.create

sealed class CreateTrucoEvents

class CreateTrucoLobbyEvent(val numberOfPlayers: Int): CreateTrucoEvents()