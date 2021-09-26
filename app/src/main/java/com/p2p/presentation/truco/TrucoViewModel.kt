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
    private val _totalPlayers = MutableLiveData<Int>(2)
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

    private val _envidoButtonEnabled = MutableLiveData(true)
    val envidoButtonEnabled: LiveData<Boolean> = _envidoButtonEnabled

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int> = _currentRound

    private lateinit var currentPlayerTurn: PlayerTeam

    private var currentActionPoints: Int = 1
    private var previousActions: List<TrucoAction> = emptyList()

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
                disableButtonsIfApplies(message.action, actionPerformer = false)
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
                !envidoButtonEnabled.requireValue()
            )
        performAction(nextTrucoAction)
    }

    fun performAction(action: TrucoAction) {
        disableButtonsIfApplies(action, actionPerformer = true)
        connection.write(TrucoActionMessage(action))
        updateActionValues(action)
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
        when (action) {
            is NoIDont -> {
                //TODO mandar mensaje para que sume los puntos al oponente
                when (previousActions.last()) {
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

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        performAction(action)
    }

    fun onGameStarted() {
        nextTurn(playersTeams.first())
    }

    fun createEnvido(alreadyReplicated: Boolean) = Envido(alreadyReplicated, previousActions)

    fun createRealEnvido() = RealEnvido(previousActions)

    // TODO: receive total opponent points
    fun createFaltaEnvido() = FaltaEnvido(0, previousActions)

    private fun newHand() {
        dispatchSingleTimeEvent(TrucoNewHand)
        currentHandWinners.clear()
        _lastTrucoAction.value = null
        _envidoButtonEnabled.value = true
        _trucoButtonEnabled.value = true
    }

    /** Updates currentActionPoints and currentAction.
     * currentAction value only will be recorded if the action received is not yes or no, in order to keep the history */
    private fun updateActionValues(action: TrucoAction) {
        when (action) {
            is YesIDo -> currentActionPoints = previousActions.last().yesPoints
            is NoIDont -> currentActionPoints = previousActions.last().noPoints
            else -> previousActions = previousActions + action
        }
    }

    //TODO Llamar cuando los puntos actuales hayan sido asignados a algun equipo
    private fun cleanActionValues() {
        currentActionPoints = 1
        previousActions = emptyList()
    }

    private fun disableButtonsIfApplies(action: TrucoAction, actionPerformer: Boolean) {
        when (action) {
            // Envido can be asked after truco on the first round, so it is not fully asked until is answered with yes or no
            is ValeCuatro -> updateTrucoValues(action, buttonEnabled = false)
            is TrucoGameAction -> updateTrucoValues(action, buttonEnabled = !actionPerformer)
            is EnvidoGoesFirst -> {
                _lastTrucoAction.value = null
                _trucoButtonEnabled.value = true
                _envidoButtonEnabled.value = false
            }
            is Envido, is RealEnvido, is FaltaEnvido, EnvidoGoesFirst ->
                _envidoButtonEnabled.value = false
            is YesIDo -> if (isAcceptingTruco()) _envidoButtonEnabled.value = false
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

        if (hasRoundFinished()) {
            onRoundFinished(currentRoundPlayedCards)
        } else {
            nextTurn()
        }
    }

    private fun onRoundFinished(currentRoundPlayedCards: List<PlayedCard>) {
        val round = _currentRound.requireValue()
        _currentRound.value = round + 1
        playedCards.add(mutableListOf())

        val roundWinnerPlayerTeam = getRoundWinnerPlayerTeam(currentRoundPlayedCards)
        val roundResult = TrucoRoundResult.get(roundWinnerPlayerTeam, myPlayerTeam)
        currentHandWinners.add(roundWinnerPlayerTeam)
        dispatchSingleTimeEvent(TrucoFinishRound(round, roundResult))

        if (hasCurrentHandFinished()) {
            onHandFinished()
        } else {
            nextTurn(roundWinnerPlayerTeam)
        }
    }

    private fun onHandFinished() {
        val handWinnerPlayerTeam = getCurrentHandWinner()
        val score = if (handWinnerPlayerTeam.team == myPlayerTeam.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
        newHand()
    }

    private fun hasRoundFinished(): Boolean {
        return playedCards.last().size == totalPlayers.requireValue() ||
                hasRoundFinishedBecauseAceOfSwords()
    }

    private fun hasRoundFinishedBecauseAceOfSwords() = playedCards.last()
        .firstOrNull { it.card == TrucoCardsChallenger.aceOfSwords }
        ?.let { it.playerTeam in currentHandWinners }
        ?: false

    private fun hasCurrentHandFinished(): Boolean {
        return currentHandWinners.size == MAX_HAND_ROUNDS ||
                groupCurrentHandWinners().any { it.size >= WINNER_HAND_ROUNDS_THRESHOLD } ||
                (currentHandWinners.any { it == null } && currentHandWinners.any { it != null })
    }

    private fun getRoundWinnerPlayerTeam(currentRoundPlayedCards: List<PlayedCard>): PlayerTeam? {
        val winnerCards = TrucoCardsChallenger.getWinnerCards(currentRoundPlayedCards.map { it.card })
        return currentRoundPlayedCards
            .filter { it.card in winnerCards }
            .takeIf { winners -> winners.size == 1 || winners.all { it.playerTeam == winners.first().playerTeam } }
            ?.first()
            ?.playerTeam
    }

    private fun getCurrentHandAbsoluteWinner() = groupCurrentHandWinners()
        .firstOrNull { it.size >= WINNER_HAND_ROUNDS_THRESHOLD }
        ?.first()

    /**
     * Ways to win a hand:
     * - Win two rounds.
     * - Win the first round and tie the second or lose the second and tie the last.
     * - Tie the first (and maybe the second) and wining the next round.
     * - Tie all the rounds and be the hand.
     */
    private fun getCurrentHandWinner(): PlayerTeam {
        if (currentHandWinners.all { it == null }) {
            TODO("Here the current round hand should be set as winner")
        }

        return getCurrentHandAbsoluteWinner()
            ?: currentHandWinners.first()
            ?: currentHandWinners.filterNotNull().first()
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

