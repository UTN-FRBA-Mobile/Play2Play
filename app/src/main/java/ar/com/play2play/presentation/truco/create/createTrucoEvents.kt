package ar.com.play2play.presentation.truco.create

sealed class CreateTrucoEvents

class GoToSelectPoints(val numberOfPlayers: Int) : CreateTrucoEvents()