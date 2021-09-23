package com.p2p.presentation.truco.create

/** A base class for all events that could occur on the categories events screen. */
sealed class TrucoCreateEvents

object StartGame: TrucoCreateEvents()

object NoConnectedPlayers : TrucoCreateEvents()