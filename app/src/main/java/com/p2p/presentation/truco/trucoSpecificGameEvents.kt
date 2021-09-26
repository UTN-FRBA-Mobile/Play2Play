package com.p2p.presentation.truco

import com.p2p.model.truco.Card
import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.truco.actions.TrucoAction

sealed class TrucoSpecificGameEvent : SpecificGameEvent()

object HandOutCards : TrucoSpecificGameEvent()

class TrucoShowMyActionEvent(val action: TrucoAction) : TrucoSpecificGameEvent()

class TrucoShowOpponentActionEvent(val action: TrucoAction) : TrucoSpecificGameEvent()

object TrucoFinishHand : TrucoSpecificGameEvent()

class TrucoFinishRound(
    val round: Int,
    val result: TrucoRoundResult
) : TrucoSpecificGameEvent()

object TrucoNewHand : TrucoSpecificGameEvent()

data class TrucoRivalPlayedCardEvent(
    val rivalPosition: TrucoRivalPosition,
    val card: Card,
    val round: Int
) : TrucoSpecificGameEvent()

object TrucoTakeTurnEvent : TrucoSpecificGameEvent()
