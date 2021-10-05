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
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.message.*
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.EnvidoGameAction
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
import com.p2p.presentation.truco.envidoCalculator.EnvidoMessageCalculator
import com.p2p.presentation.truco.envidoCalculator.EnvidoPointsCalculator

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

    /** Set the quantity of players selected by the user when creating the game . */
    private val _totalPlayers = MutableLiveData<Int>()
    val totalPlayers: LiveData<Int> = _totalPlayers

    /** Current cards for this player */
    protected val _myCards = MutableLiveData<List<Card>>()
    val myCards: LiveData<List<Card>> = _myCards

    /** Cards being used by each player in a hand  */
    protected var cardsByPlayer = listOf<PlayerWithCards>()

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
    protected var previousActions: List<TrucoAction> = emptyList()

    protected val myPlayerTeam: PlayerTeam by lazy { playersTeams.first { it.player == userName } }
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
                val playerPosition =
                    TrucoPlayerPosition.get(message.playerTeam, playersTeams, myPlayerTeam)
                dispatchSingleTimeEvent(
                    TrucoShowOpponentActionEvent(
                        message.action,
                        playerPosition
                    )
                )
                onActionDone(message.action, message.playerTeam.team)
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
        connection.write(TrucoActionMessage(action, myPlayerTeam))
        updateActionValues(action)
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
        onActionDone(action, myPlayerTeam.team)
    }

    fun performEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, Envido(previousActions))

    fun performRealEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, RealEnvido(previousActions))

    // TODO: receive total opponent points
    fun performFaltaEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, FaltaEnvido(0, previousActions))

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        performAction(action)
    }

    fun onGameStarted() {
        nextTurn(playersTeams.first())
    }

    private fun performOrReplyAction(isReply: Boolean, action: TrucoAction) {
        if (isReply)
            replyAction(action)
        else
            performAction(action)
    }

    fun playCard(card: Card) {
        val playedCard = PlayedCard(myPlayerTeam, card)
        connection.write(TrucoPlayCardMessage(playedCard))
        onCardPlayed(playedCard)
    }

    private fun newHand() {
        dispatchSingleTimeEvent(TrucoNewHand)
        cleanActionValues()
        currentHandWinners.clear()
        _lastTrucoAction.value = null
        _envidoButtonEnabled.value = true
        _trucoButtonEnabled.value = true
    }

    /**
     * Updates currentActionPoints and currentAction.
     * currentAction value only will be recorded if the action received is not yes or no, in order to keep the history.
     */
    private fun updateActionValues(action: TrucoAction) {
        when (action) {
            is YesIDo -> currentActionPoints = previousActions.last().yesPoints
            is NoIDont -> currentActionPoints = previousActions.last().noPoints
            else -> previousActions = previousActions + action
        }
    }

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
            is EnvidoGameAction ->
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

    fun setTotalPlayers(players: Int) {
        _totalPlayers.value = players;
    }

    private fun onRivalCardPlayed(playedCard: PlayedCard) {
        dispatchSingleTimeEvent(
            TrucoOtherPlayedCardEvent(
                TrucoPlayerPosition.get(playedCard.playerTeam, playersTeams, myPlayerTeam),
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

    private fun onHandFinished(handWinnerPlayerTeam: Int = getCurrentHandWinner().team) {
        updateScore(handWinnerPlayerTeam)
        newHand()
    }

    private fun updateScore(winnerTeam: Int) {
        val score = if (winnerTeam == myPlayerTeam.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
    }

    private fun hasRoundFinished(): Boolean {
        return playedCards.last().size == totalPlayers.requireValue() ||
                hasRoundFinishedBecauseAceOfSwords() ||
                hasRoundFinishedBecauseFullTeamLost()
    }

    private fun hasRoundFinishedBecauseAceOfSwords() = playedCards.last()
        .firstOrNull { it.card == TrucoCardsChallenger.aceOfSwords }
        ?.let { it.playerTeam in currentHandWinners || currentHandWinners.any { winner -> winner == null } }
        ?: false

    private fun hasRoundFinishedBecauseFullTeamLost(): Boolean {
        if (currentHandWinners.isEmpty()) {
            return false // If there's no hand finished yet, skip this condition
        }
        val currentRoundPlayedCards = playedCards.last()
        val teamWithRoundFinished = currentRoundPlayedCards
            .groupBy { it.playerTeam.team }
            .values
            .firstOrNull { it.size == totalPlayers.requireValue() / 2 }
            ?.first()
            ?.playerTeam
            ?.team
            ?: return false // If there's no a full team that finished the round, skip this condition
        val roundWinner = getRoundWinnerPlayerTeam(currentRoundPlayedCards)?.team
        val hasLostCurrentRound = roundWinner != null && roundWinner != teamWithRoundFinished
        val hasTieCurrentRound = roundWinner == null
        val hasWonAnyRound = currentHandWinners.any { it?.team == teamWithRoundFinished }
        val hasLostAnyRound =
            currentHandWinners.any { it != null && it.team != teamWithRoundFinished }
        return (hasLostCurrentRound && !hasWonAnyRound) || (hasTieCurrentRound && hasLostAnyRound)
    }

    private fun hasCurrentHandFinished(): Boolean {
        return currentHandWinners.size == MAX_HAND_ROUNDS ||
                groupCurrentHandWinners().any { it.size >= WINNER_HAND_ROUNDS_THRESHOLD } ||
                (currentHandWinners.any { it == null } && currentHandWinners.any { it != null })
    }

    private fun getRoundWinnerPlayerTeam(currentRoundPlayedCards: List<PlayedCard>): PlayerTeam? {
        val winnerCards =
            TrucoCardsChallenger.getWinnerCards(currentRoundPlayedCards.map { it.card })
        return currentRoundPlayedCards
            .filter { it.card in winnerCards }
            .takeIf { winners -> winners.all { it.playerTeam == winners.first().playerTeam } }
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
            return playedCards.first()
                .first().playerTeam // The first player that played a card is the hand
        }

        return getCurrentHandAbsoluteWinner() ?: currentHandWinners.filterNotNull().first()
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

    open protected fun onActionDone(action: TrucoAction, performedByTeam: Int) {
        when (action) {
            is NoIDont -> onNoIDont(performedByTeam)
            is YesIDo -> {
                if (previousActions.last() is EnvidoGameAction) {
                    playEnvido()
                }
            }
        }
    }


    private fun playEnvido() {
        val pointsByPlayer = cardsByPlayer.map {
            val playerTeam = playersTeams.first { playerTeam -> playerTeam.player == it.player }
            playerTeam to EnvidoPointsCalculator.getPoints(it.cards)
        }
        val roundOrder = getRoundOrder()
        val playersWithPoints = roundOrder.map { player -> pointsByPlayer.first { it.first ==  player} }

        val winner = getWinner(pointsByPlayer, roundOrder)

        val actionsByPlayer: Map<PlayerTeam, TrucoAction?> = if(roundOrder.size == 2){
            EnvidoMessageCalculator.envidoMessagesFor2(playersWithPoints)
        }else{
            EnvidoMessageCalculator.envidoMessagesFor4(playersWithPoints)
        }

        val actionsByPosition = actionsByPlayer.filter { it.value != null }.map { actionByPlayer ->
            val playerPosition = TrucoPlayerPosition.get(actionByPlayer.key, playersTeams, myPlayerTeam)
            playerPosition to actionByPlayer.value!!
        }.toMap()

        dispatchSingleTimeEvent(TrucoShowManyActionsEvent(actionsByPosition))
        onEnvidoPlayed(winner.first)
    }

    private fun getWinner(
        pointsByPlayer: List<Pair<PlayerTeam, Int>>,
        roundOrder: List<PlayerTeam>
    ): Pair<PlayerTeam, Int> {
        val winnerPoints = pointsByPlayer.maxByOrNull { it.second }!!.second
        val playersWithMaxPoints = pointsByPlayer.filter { it.second == winnerPoints }
        val winnerName = roundOrder.first {
            playersWithMaxPoints.map { it.first.player }.contains(it.player)
        }.player

        return pointsByPlayer.first { it.first.player == winnerName }
    }

    private fun getRoundOrder(): List<PlayerTeam> {
        //TODO falta poner la mano cuando lo haga juan :)
        val handIndex = 0
        return playersTeams.drop(handIndex) + playersTeams.take(handIndex)
    }


    private fun onEnvidoPlayed(winner: PlayerTeam) {
        val score = if (myPlayerTeam.team == winner.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
    }

    private fun onNoIDont(performedByTeam: Int) {
        val winner = playersTeams.first { it.team != performedByTeam }.team
        when (previousActions.last()) {
            is Truco, is Retruco, is ValeCuatro -> {
                onHandFinished(winner)
            }
            is EnvidoGameAction -> {
                updateScore(winner)
            }
        }
    }

    companion object {

        private const val MAX_HAND_ROUNDS = 3
        private const val WINNER_HAND_ROUNDS_THRESHOLD = 2
    }
}
