package com.p2p.presentation.tuttifrutti.play

sealed class TuttiFruttiPlayingEvents

object ShowInvalidInputs : TuttiFruttiPlayingEvents()

object FinishRound : TuttiFruttiPlayingEvents()