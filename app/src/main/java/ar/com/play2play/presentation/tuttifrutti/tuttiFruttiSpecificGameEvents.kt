package ar.com.play2play.presentation.tuttifrutti

import ar.com.play2play.presentation.basegame.SpecificGameEvent

sealed class TuttiFruttiSpecificGameEvent : SpecificGameEvent()

object ObtainWords : TuttiFruttiSpecificGameEvent()

object GoToReview : TuttiFruttiSpecificGameEvent()

object GoToClientReview : TuttiFruttiSpecificGameEvent()

object GoToFinalScore : TuttiFruttiSpecificGameEvent()
