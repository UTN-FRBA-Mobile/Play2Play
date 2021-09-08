package com.p2p.presentation.truco

import com.p2p.presentation.basegame.SpecificGameEvent

sealed class TrucoSpecificGameEvent : SpecificGameEvent()

object HandOutCards : TrucoSpecificGameEvent()
