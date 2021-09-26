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
import com.p2p.model.truco.PlayerTeam
import com.p2p.model.truco.message.TrucoActionMessage
import com.p2p.model.truco.message.TrucoPlayCardMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoAction.Envido
import com.p2p.presentation.truco.actions.TrucoAction.EnvidoGoesFirst
import com.p2p.presentation.truco.actions.TrucoAction.FaltaEnvido
import com.p2p.presentation.truco.actions.TrucoAction.NoIDont
import com.p2p.presentation.truco.actions.TrucoAction.RealEnvido
import com.p2p.presentation.truco.actions.TrucoAction.Retruco
import com.p2p.presentation.truco.actions.TrucoAction.Truco
import com.p2p.presentation.truco.actions.TrucoAction.ValeCuatro
import com.p2p.presentation.truco.actions.TrucoAction.YesIDo
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

    private val _ourScore = MutableLiveData<Int>()
    val ourScore: LiveData<Int> = _ourScore

    private val _theirScore = MutableLiveData<Int>()
    val theirScore: LiveData<Int> = _theirScore

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

    private lateinit var currentPlayerTurn: PlayerTeam

    private var currentActionPoints: Int = 1
    private var currentAction: TrucoAction? = null

    private val myPlayerTeam: PlayerTeam by lazy { playersTeams.first { it.player == userName } }
    private val playedCards: MutableList<MutableList<PlayedCard>> = mutableListOf(mutableListOf())
    private val currentHandWinners: MutableList<PlayerTeam?> = mutableListOf()

    init {
        _ourScore.value = 0
        _theirScore.value = 0
    }

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
            is TrucoPlayCardMessage -> onRivalCardPlayed(message.playedCard)
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
        currentHandWinners.clear()
        _lastTrucoAction.value = null
        _envidoDisabled.value = false
        _trucoButtonEnabled.value = true
    }

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        performAction(action)
    }

    fun onGameStarted() {
        nextTurn(playersTeams.first())
    }

    /** Updates currentActionPoints and currentAction.
     * currentAction value only will be replaced if the action received is not yes or no, in order to keep the history */
    private fun updateActionValues(action: TrucoAction) {
        currentActionPoints += action.points
        currentAction = if (listOf(YesIDo, NoIDont).contains(action)) currentAction else action
    }

    //TODO Llamar cuando los puntos actuales hayan sido asignados a algun equipo
    private fun cleanActionValues() {
        currentActionPoints = 1
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

    fun playCard(card: Card) {
        val playedCard = PlayedCard(myPlayerTeam, card)
        connection.write(TrucoPlayCardMessage(playedCard))
        onCardPlayed(playedCard)
    }

    private fun onRivalCardPlayed(playedCard: PlayedCard) {
        dispatchSingleTimeEvent(
            TrucoRivalPlayedCardEvent(
                TrucoRivalPosition.get(playedCard.playerTeam, playersTeams, myPlayerTeam),
                playedCard.card,
                currentRound.requireValue()
            )
        )
        onCardPlayed(playedCard)
    }

    private fun onCardPlayed(playedCard: PlayedCard) {
        val currentRoundPlayedCards = playedCards.last()
        currentRoundPlayedCards.add(playedCard)

        if (hasRoundFinished(currentRoundPlayedCards)) {
            onRoundFinished(currentRoundPlayedCards)
        } else {
            nextTurn()
        }
    }

    private fun onRoundFinished(currentRoundPlayedCards: List<PlayedCard>) {
        _currentRound.value = _currentRound.value?.plus(1)
        playedCards.add(mutableListOf())

        val roundWinnerPlayerTeam = getRoundWinnerPlayerTeam(currentRoundPlayedCards)
        currentHandWinners.add(roundWinnerPlayerTeam)

        if (hasCurrentHandFinished()) {
            onHandFinished()
        } else {
            nextTurn(roundWinnerPlayerTeam)
        }
    }

    private fun onHandFinished() {
        val handWinnerPlayerTeam = getCurrentHandWinner()
        val score = if (handWinnerPlayerTeam == myPlayerTeam.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
        newHand()
    }

    private fun hasRoundFinished(currentRoundPlayedCards: List<PlayedCard>): Boolean {
        return currentRoundPlayedCards.size == totalPlayers.requireValue()
    }

    private fun hasCurrentHandFinished(): Boolean {
        return currentHandWinners.size == MAX_HAND_ROUNDS ||
                groupCurrentHandWinners().any { it.size >= WINNER_HAND_ROUNDS_THRESHOLD } ||
                (currentHandWinners.first() == null && currentHandWinners.count { it == null } > 0)
    }

    private fun getRoundWinnerPlayerTeam(currentRoundPlayedCards: List<PlayedCard>): PlayerTeam? {
        val winnerCard = TrucoCardsChallenger.getWinnerCard(currentRoundPlayedCards.map { it.card })
        return currentRoundPlayedCards
            .filter { it.card == winnerCard }
            .takeIf { winners -> winners.size == 1 || winners.all { it.playerTeam == winners.first().playerTeam } }
            ?.first()
            ?.playerTeam
    }

    private fun getCurrentHandAbsoluteWinner() = groupCurrentHandWinners()
        .firstOrNull { it.size >= WINNER_HAND_ROUNDS_THRESHOLD }
        ?.first()
        ?.team

    /**
     * Ways to win a hand:
     * - Win two rounds.
     * - Win the first round and tie the second or lose the second and tie the last.
     * - Tie the first (and maybe the second) and wining the next round.
     * - Tie all the rounds and be the hand.
     */
    private fun getCurrentHandWinner(): Int {
        if (currentHandWinners.all { it == null }) {
            TODO("Here the current round hand should be set as winner")
        }

        return getCurrentHandAbsoluteWinner()
            ?: currentHandWinners.first()?.team
            ?: currentHandWinners.filterNotNull().first().team
    }

    private fun groupCurrentHandWinners() = currentHandWinners
        .groupBy { it?.team }
        .filterKeys { it != null }
        .values

    private fun nextTurn(forceNextTurnPlayer: PlayerTeam? = null) {
        setCurrentPlayerTurn(forceNextTurnPlayer ?: getNextPlayerTurn())
    }

    private fun getNextPlayerTurn(): PlayerTeam {
        val nextIndex = playersTeams.indexOf(currentPlayerTurn) + 1
        return playersTeams[nextIndex % playersTeams.size]
    }

    private fun setCurrentPlayerTurn(player: PlayerTeam) {
        currentPlayerTurn = player
        if (player == myPlayerTeam) {
            dispatchSingleTimeEvent(TrucoTakeTurnEvent)
        }
    }

    companion object {

        private const val MAX_HAND_ROUNDS = 3
        private const val WINNER_HAND_ROUNDS_THRESHOLD = 2
    }
}

