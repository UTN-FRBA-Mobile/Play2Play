package com.p2p.presentation.truco

import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.truco.actions.TrucoAction

sealed class TrucoSpecificGameEvent : SpecificGameEvent()

class TrucoShowMyActionEvent(val action: TrucoAction) : TrucoSpecificGameEvent()

object HandOutCards : TrucoSpecificGameEvent()
