package com.p2p.presentation.tuttifrutti

import com.p2p.presentation.basegame.SpecificGameEvent

sealed class TuttiFruttiSpecificGameEvent : SpecificGameEvent()

object GoToReview : TuttiFruttiSpecificGameEvent()
