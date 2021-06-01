package com.p2p.presentation.tuttifrutti.create.rounds

sealed class RoundsNumberEvent

class GoToTuttiFruttiLobby(val totalRounds: Int): RoundsNumberEvent()