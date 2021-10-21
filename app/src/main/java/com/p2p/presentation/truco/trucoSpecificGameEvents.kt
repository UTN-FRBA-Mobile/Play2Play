package com.p2p.presentation.truco

import com.p2p.model.truco.Card
import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.truco.actions.TrucoAction

sealed class TrucoSpecificGameEvent : SpecificGameEvent()

object TrucoGoToBuildTeams : TrucoSpecificGameEvent()

data class TrucoGoToPlay(val playersQuantity: Int) : TrucoSpecificGameEvent()

class TrucoShowMyActionEvent(
    action: TrucoAction,
    onComplete: () -> Unit = {}
) : TrucoShowActionEvent(action, TrucoPlayerPosition.MY_SELF, false, onComplete)

open class TrucoShowActionEvent(
    val action: TrucoAction,
    val playerPosition: TrucoPlayerPosition,
    val canAnswer: Boolean,
    val onComplete: () -> Unit = {}
) : TrucoSpecificGameEvent()

data class TrucoShowManyActionsEvent(
    val actionByPlayer: Map<TrucoPlayerPosition, TrucoAction>,
    val onComplete: () -> Unit
) : TrucoSpecificGameEvent()

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

object TrucoFinishGame : TrucoSpecificGameEvent()

data class TrucoShowEarnedPoints(
    val isMyTeam: Boolean,
    val earnedPoints: Int,
    val onComplete: () -> Unit
) : TrucoSpecificGameEvent()
