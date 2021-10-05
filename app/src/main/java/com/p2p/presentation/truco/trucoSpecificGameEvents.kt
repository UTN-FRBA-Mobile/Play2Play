package com.p2p.presentation.truco

import com.p2p.model.truco.Card
import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.truco.actions.TrucoAction

sealed class TrucoSpecificGameEvent : SpecificGameEvent()

data class TrucoGoToPlay(val playersQuantity: Int) : TrucoSpecificGameEvent()

data class TrucoShowMyActionEvent(val action: TrucoAction) : TrucoSpecificGameEvent()

data class TrucoShowOpponentActionEvent(
    val action: TrucoAction,
    val playerPosition: TrucoPlayerPosition,
    val canAnswer: Boolean
) : TrucoSpecificGameEvent()

data class TrucoShowManyActionsEvent(val actionByPlayer: Map<TrucoPlayerPosition, TrucoAction>) :
    TrucoSpecificGameEvent()

data class TrucoFinishRound(
    val round: Int,
    val result: TrucoRoundResult
) : TrucoSpecificGameEvent()

object TrucoNewHand : TrucoSpecificGameEvent()

data class TrucoOtherPlayedCardEvent(
    val playerPosition: TrucoPlayerPosition,
    val card: Card,
    val round: Int
) : TrucoSpecificGameEvent()


object TrucoTakeTurnEvent : TrucoSpecificGameEvent()
