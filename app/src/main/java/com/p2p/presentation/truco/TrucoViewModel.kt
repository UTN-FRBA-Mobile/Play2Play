package com.p2p.presentation.truco

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.PlayerTeam
import com.p2p.model.truco.Card
import com.p2p.model.truco.message.TrucoActionMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoAction.*
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses
import com.p2p.presentation.truco.actions.TrucoGameAction

abstract class TrucoViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : GameViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository,
    Game.TRUCO
) {
    /** List with the teams of players */
    protected lateinit var playersTeams: List<PlayerTeam>

    // TODO: Must set this value on truco game creation
    /** Set the quantity of players selected by the user when creating the game . */
    //TODO this is a mock!!!!
    protected val _totalPlayers = MutableLiveData<Int>(2)
    val totalPlayers: LiveData<Int> = _totalPlayers

    /** Current cards for this player */
    protected val _myCards = MutableLiveData<List<Card>>()
    val myCards: LiveData<List<Card>> = _myCards

    var currentActionPoints: Int = 0

    var currentAction: TrucoAction? = null

    private val _actionAvailableResponses = MutableLiveData<TrucoActionAvailableResponses>()
    val actionAvailableResponses: LiveData<TrucoActionAvailableResponses> =
        _actionAvailableResponses

    private val _lastTrucoAction = MutableLiveData<TrucoGameAction?>(null)
    val lastTrucoAction: LiveData<TrucoGameAction?> = _lastTrucoAction

    private val _trucoButtonEnabled = MutableLiveData(true)
    val trucoButtonEnabled: LiveData<Boolean> = _trucoButtonEnabled

    private val _envidoDisabled = MutableLiveData(false)
    val envidoDisabled: LiveData<Boolean> = _envidoDisabled

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int> = _currentRound

    abstract override fun startGame()

    override fun goToPlay() {
        gameAlreadyStarted = true
        super.goToPlay()
    }

    /** This will only be used by the server */
    protected open fun handOutCards() {}

    @CallSuper
    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TrucoActionMessage -> {
                setTrucoOrEnvidoAsAskedIfApplies(message.action, actionPerformer = false)
                updateActionValues(message.action)
                dispatchSingleTimeEvent(TrucoShowOpponentActionEvent(message.action))
                // TODO: si se recibe quiero o no quiero, calcular los puntos
            }
        }
    }

    fun performTruco() {
        val nextTrucoAction = _lastTrucoAction.value?.nextAction()
            ?: Truco(
                currentRound.requireValue(),
                envidoDisabled.requireValue()
            )
        performAction(nextTrucoAction)
    }

    fun performAction(action: TrucoAction) {
        setTrucoOrEnvidoAsAskedIfApplies(action, actionPerformer = true)
        connection.write(TrucoActionMessage(action))
        updateActionValues(action)
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
        when (action) {
            is NoIDont -> {
                //TODO mandar mensaje para que sume los puntos al oponente
                when (currentAction) {
                    is Truco, is Retruco, is ValeCuatro -> dispatchSingleTimeEvent(TrucoFinishHand)
                }
                cleanActionValues()
            }
            is YesIDo -> {
                //TODO mandar mensaje para jugar y sumar puntos
                cleanActionValues()
            }
        }
    }

    fun newHand() {
        dispatchSingleTimeEvent(TrucoNewHand)
        _lastTrucoAction.value = null
        _envidoDisabled.value = false
        _trucoButtonEnabled.value = true
    }

    fun finishRound() {
        _currentRound.value = _currentRound.value?.plus(1)
    }


    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        performAction(action)
    }

    /** Updates currentActionPoints and currentAction.
     * currentAction value only will be replaced if the action received is not yes or no, in order to keep the history */
    private fun updateActionValues(action: TrucoAction) {
        currentActionPoints += action.points
        currentAction = if (listOf(YesIDo, NoIDont).contains(action)) currentAction else action
    }

    //TODO Llamar cuando los puntos actuales hayan sido asignados a algun equipo
    private fun cleanActionValues() {
        currentActionPoints = 0
        currentAction = null
    }

    private fun setTrucoOrEnvidoAsAskedIfApplies(action: TrucoAction, actionPerformer: Boolean) {
        when (action) {
            // Envido can be asked after truco on the first round, so it is not fully asked until is answered with yes or no
            is ValeCuatro -> updateTrucoValues(action, buttonEnabled = false)
            is TrucoGameAction -> updateTrucoValues(action, buttonEnabled = !actionPerformer)
            is EnvidoGoesFirst -> {
                _lastTrucoAction.value = null
                _trucoButtonEnabled.value = true
                _envidoDisabled.value = true
            }
            is Envido, is RealEnvido, is FaltaEnvido, EnvidoGoesFirst -> _envidoDisabled.value =
                true
            is YesIDo -> if (isAcceptingTruco()) _envidoDisabled.value = true
            else -> Unit
        }
    }

    private fun isAcceptingTruco() = _lastTrucoAction.value != null
    private fun updateTrucoValues(action: TrucoGameAction, buttonEnabled: Boolean) {
        _lastTrucoAction.value = action
        _trucoButtonEnabled.value = buttonEnabled
    }


}

