package com.p2p.presentation.truco

import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.truco.actions.TrucoAction

sealed class TrucoSpecificGameEvent : SpecificGameEvent()

object HandOutCards : TrucoSpecificGameEvent()

class TrucoShowMyActionEvent(val action: TrucoAction) : TrucoSpecificGameEvent()
class TrucoShowOpponentActionEvent(val action: TrucoAction) : TrucoSpecificGameEvent()
object TrucoFinishRound : TrucoSpecificGameEvent()
