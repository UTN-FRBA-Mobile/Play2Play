package com.p2p.presentation.tuttifrutti

import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.presentation.basegame.SpecificGameEvent

sealed class TuttiFruttiSpecificGameEvent : SpecificGameEvent()

object ObtainWords : TuttiFruttiSpecificGameEvent()

class GoToReview(val finishedRoundInfo: List<FinishedRoundInfo>) : TuttiFruttiSpecificGameEvent()
