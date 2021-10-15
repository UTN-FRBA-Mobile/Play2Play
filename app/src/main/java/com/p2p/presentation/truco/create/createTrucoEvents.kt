package com.p2p.presentation.truco.create

sealed class CreateTrucoEvents

class GoToSelectPoints(val numberOfPlayers: Int) : CreateTrucoEvents()