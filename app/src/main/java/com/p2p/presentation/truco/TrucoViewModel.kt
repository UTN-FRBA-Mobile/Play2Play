package com.p2p.presentation.truco

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.Card
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.message.TrucoActionMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoAction.*
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses
import com.p2p.utils.Logger

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

    /** Current cards for this player */
    protected val _myCards = MutableLiveData<List<Card>>()
    val myCards: LiveData<List<Card>> = _myCards

    var currentActionPoints: Int = 0

    var currentAction: TrucoAction? = null

    private val trucazoActions = listOf(Trucazo, Retrucazo, ValeCuatro)

    private val _actionAvailableResponses = MutableLiveData<TrucoActionAvailableResponses>()
    val actionAvailableResponses: LiveData<TrucoActionAvailableResponses> =
        _actionAvailableResponses

    private val _trucoAlreadyAsked = MutableLiveData(false)
    val trucoAlreadyAsked: LiveData<Boolean> = _trucoAlreadyAsked

    private val _envidoAlreadyAsked = MutableLiveData(false)
    val envidoAlreadyAsked: LiveData<Boolean> = _envidoAlreadyAsked

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int> = _currentRound

    abstract override fun startGame()

    override fun goToPlay() {
        gameAlreadyStarted = true
        super.goToPlay()
    }

    /** This will only be used by the server */
    protected open fun handOutCards() {}

    protected fun getCardsForPlayer(playersWithCards: List<PlayerWithCards>, player: String) =
        playersWithCards.first { it.player == player }.cards

    @CallSuper
    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TrucoActionMessage -> {
                updateActionValues(message.action)
                dispatchSingleTimeEvent(TrucoShowOpponentActionEvent(message.action))
                setTrucoOrEnvidoAsAskedIfApplies(message.action)
            }
        }
    }

    fun performAction(action: TrucoAction) {
        when (action) {
            is NoIDont -> {
                //TODO mandar mensaje para que sume los puntos al oponente
                cleanActionValues()
                if (trucazoActions.contains(currentAction))
                    dispatchSingleTimeEvent(TrucoFinishHand)
            }
            is YesIDo -> {
                //TODO mandar mensaje para jugar y sumar puntos
                cleanActionValues()
            }
        }
        setTrucoOrEnvidoAsAskedIfApplies(action)
        connection.write(TrucoActionMessage(action))
        updateActionValues(action)
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
    }

    fun newHand() {
        dispatchSingleTimeEvent(TrucoNewHand)
        _trucoAlreadyAsked.value = false
        _envidoAlreadyAsked.value = false
    }

    fun finishRound() {
        _currentRound.value?.plus(1)
    }

    protected fun updateActionValues(action: TrucoAction) {
        currentActionPoints += action.points
        currentAction = action
    }

    //TODO Llamar cuando los puntos actuales hayan sido asignados a algun equipo
    protected fun cleanActionValues() {
        currentActionPoints = 0
        currentAction = null
    }

    fun setTrucoOrEnvidoAsAskedIfApplies(action: TrucoAction) {
        Logger.d("P2P_TrucoViewModel", "actionApplies, action: $action")
        when (action) {
            is Trucazo -> _trucoAlreadyAsked.value = true
            is Envido, is RealEnvido, is FaltaEnvido -> _envidoAlreadyAsked.value = true
            else -> Unit
        }
    }

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        performAction(action)
    }
}

