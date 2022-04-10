package ar.com.play2play.presentation.tuttifrutti.play

sealed class TuttiFruttiPlayingEvents

object ShowInvalidInputs : TuttiFruttiPlayingEvents()

object FinishRound : TuttiFruttiPlayingEvents()