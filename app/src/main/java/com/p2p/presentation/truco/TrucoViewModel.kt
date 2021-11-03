package com.p2p.presentation.truco

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.truco.Card
import com.p2p.model.truco.PlayerWithCards
import com.p2p.model.truco.TeamPlayer
import com.p2p.model.truco.message.TrucoActionMessage
import com.p2p.model.truco.message.TrucoPlayCardMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.basegame.PlayersRecoverability
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.EnvidoGameAction
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoAction.*
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses
import com.p2p.presentation.truco.actions.TrucoGameAction
import com.p2p.presentation.truco.envidoCalculator.EnvidoMessageCalculator
import com.p2p.presentation.truco.envidoCalculator.EnvidoPointsCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override val maxPlayersOnRoom: Int
        get() = totalPlayers.requireValue()

    override val playersRecoverability = PlayersRecoverability.MUST_BE_RECOVERED

    /** List with the teams of players */
    protected lateinit var teamPlayers: List<TeamPlayer>

    /** Set the quantity of players selected by the user when creating the game . */
    private val _totalPlayers = MutableLiveData<Int>()
    val totalPlayers: LiveData<Int> = _totalPlayers

    /** Set the quantity of points selected by the user when creating the game . */
    private val _totalPoints = MutableLiveData<Int>()
    val totalPoints: LiveData<Int> = _totalPoints

    /** Current cards for this player */
    protected val _myCards = MutableLiveData<List<Card>>()
    val myCards: LiveData<List<Card>> = _myCards

    /** Cards being used by each player in a hand  */
    protected var cardsByPlayer = listOf<PlayerWithCards>()

    protected val _ourScore = MutableLiveData<Int>()
    val ourScore: LiveData<Int> = _ourScore

    protected val _theirScore = MutableLiveData<Int>()
    val theirScore: LiveData<Int> = _theirScore

    private val _actionAvailableResponses = MutableLiveData<TrucoActionAvailableResponses>()
    val actionAvailableResponses: LiveData<TrucoActionAvailableResponses> =
        _actionAvailableResponses

    private val _lastTrucoAction = MutableLiveData<TrucoGameAction?>(null)
    val lastTrucoAction: LiveData<TrucoGameAction?> = _lastTrucoAction

    private val _trucoButtonEnabled = MutableLiveData<Boolean>()
    val trucoButtonEnabled: LiveData<Boolean> = _trucoButtonEnabled

    private val _envidoButtonEnabled = MutableLiveData<Boolean>()
    val envidoButtonEnabled: LiveData<Boolean> = _envidoButtonEnabled

    private var envidoDisabledForHand = false

    private val _currentTurnPlayerPosition = MutableLiveData<TrucoPlayerPosition>()
    val currentTurnPlayerPosition: LiveData<TrucoPlayerPosition> = _currentTurnPlayerPosition

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int> = _currentRound

    private val _playersPositions = MutableLiveData<List<Pair<TrucoPlayerPosition, String>>>()
    val playersPositions: LiveData<List<Pair<TrucoPlayerPosition, String>>> = _playersPositions

    private val _trucoAccumulatedPoints = MutableLiveData<Int>()
    val trucoAccumulatedPoints: LiveData<Int> = _trucoAccumulatedPoints

    private val _currentHandPlayerPosition = MutableLiveData<TrucoPlayerPosition>()
    val currentHandPlayerPosition: LiveData<TrucoPlayerPosition> = _currentHandPlayerPosition
    
    protected lateinit var handPlayer: TeamPlayer
        private set

    private lateinit var currentTurnPlayer: TeamPlayer

    private var currentActionPoints: Int = 1
    private var previousActions: List<TrucoAction> = emptyList()

    protected val myTeamPlayer: TeamPlayer by lazy { teamPlayers.first { it.name == userName } }
    protected val rivalTeam: Int by lazy { teamPlayers.first { it.team != myTeamPlayer.team }.team }
    private val playedCards: MutableList<MutableList<PlayedCard>> = mutableListOf(mutableListOf())
    private val currentHandWinners: MutableList<TeamPlayer?> = mutableListOf()

    private var hasAlreadyDispatchedNewHand = false
    private var isAbleToReadMessagesBecauseNewHand = false
    private var newHandPendingMessagesReceived: List<Conversation> = emptyList()

    private var isAbleToReadMessagesBecauseBackground = true
    private var backgroundPendingMessagesReceived: List<Conversation> = emptyList()

    init {
        _ourScore.value = 0
        _theirScore.value = 0
        _trucoAccumulatedPoints.value = 1
    }

    abstract fun startGame(players: List<String>)

    fun goToPlayTruco() {
        gameAlreadyStarted = true
        dispatchSingleTimeEvent(TrucoGoToPlay(totalPlayers.requireValue()))
    }

    fun goToBuildTeams() {
        dispatchSingleTimeEvent(TrucoGoToBuildTeams)
    }

    fun onResume() {
        backgroundPendingMessagesReceived.forEach { acceptMessage(it, isAbleToReadMessagesBecauseBackground = true) }
        backgroundPendingMessagesReceived = emptyList()
        isAbleToReadMessagesBecauseBackground = true
    }

    fun onStop() {
        isAbleToReadMessagesBecauseBackground = false
    }

    /** This will only be used by the server */
    open fun handOutCards() {}

    @CallSuper
    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        acceptMessage(conversation)
    }

    private fun acceptMessage(
        conversation: Conversation,
        isAbleToReadMessagesBecauseNewHand: Boolean = this.isAbleToReadMessagesBecauseNewHand,
        isAbleToReadMessagesBecauseBackground: Boolean = this.isAbleToReadMessagesBecauseBackground
    ) {
        if (!isAbleToReadMessagesBecauseNewHand) {
            newHandPendingMessagesReceived = newHandPendingMessagesReceived + conversation
            return
        } else if (!isAbleToReadMessagesBecauseBackground) {
            backgroundPendingMessagesReceived = backgroundPendingMessagesReceived + conversation
            return
        }
        when (val message = conversation.lastMessage) {
            is TrucoActionMessage -> {
                disableButtonsIfApplies(
                    message.action,
                    actionPerformer = actionWasMadeByMyTeam(message.teamPlayer)
                )
                updateActionValues(message.action)
                val playerPosition =
                    TrucoPlayerPosition.get(message.teamPlayer, teamPlayers, myTeamPlayer)
                dispatchSingleTimeEvent(
                    TrucoShowActionEvent(
                        message.action,
                        playerPosition,
                        canAnswer(message.teamPlayer)
                    ) {
                        onActionDone(message.action, message.teamPlayer.team)
                    }
                )
            }
            is TrucoPlayCardMessage -> onRivalCardPlayed(message.playedCard)
        }
    }

    fun performTruco() {
        val nextTrucoAction = _lastTrucoAction.value?.nextAction()
            ?: Truco(
                envidoGoesFirstAllowed = currentRound.requireValue() == 1 &&
                        previousActions.none { it is EnvidoGameAction }
            )
        performAction(nextTrucoAction)
    }

    fun performAction(action: TrucoAction) {
        disableButtonsIfApplies(action, actionPerformer = true)
        connection.write(TrucoActionMessage(action, myTeamPlayer))
        updateActionValues(action)
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action) {
            onActionDone(action, myTeamPlayer.team)
        })
    }

    fun performEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, Envido(previousActions))

    fun performRealEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, RealEnvido(previousActions))

    fun performFaltaEnvido(isReply: Boolean = false) =
        performOrReplyAction(
            isReply,
            FaltaEnvido(theirScore.requireValue(), ourScore.requireValue(), previousActions)
        )

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        performAction(action)
    }

    fun onMyCardsLoad() {
        nextTurn(handPlayer)
        newHandPendingMessagesReceived.forEach { acceptMessage(it, isAbleToReadMessagesBecauseNewHand = true) }
        newHandPendingMessagesReceived = emptyList()
        isAbleToReadMessagesBecauseNewHand = hasAlreadyDispatchedNewHand
    }

    private fun performOrReplyAction(isReply: Boolean, action: TrucoAction) {
        if (isReply)
            replyAction(action)
        else
            performAction(action)
    }

    fun playCard(card: Card) {
        val playedCard = PlayedCard(myTeamPlayer, card)
        connection.write(TrucoPlayCardMessage(playedCard))
        onCardPlayed(playedCard)
    }

    protected fun setPlayers(teamPlayers: List<TeamPlayer>) {
        this.teamPlayers = teamPlayers
        _playersPositions.value = teamPlayers.map {
            TrucoPlayerPosition.get(it, teamPlayers, myTeamPlayer) to it.name
        }
    }

    protected fun newHand(myCards: List<Card>) {
        isAbleToReadMessagesBecauseNewHand = false
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) { delay(NEW_HAND_DELAY_TIME_MS) }
            hasAlreadyDispatchedNewHand = true
            dispatchSingleTimeEvent(TrucoNewHand)
            cleanActionValues()
            currentHandWinners.clear()
            playedCards.clear()
            playedCards.add(mutableListOf())
            _lastTrucoAction.value = null
            _envidoButtonEnabled.value = true
            _trucoButtonEnabled.value = true
            _currentRound.value = 1
            envidoDisabledForHand = false
            _myCards.value = myCards
            _trucoAccumulatedPoints.value = 1
            checkIfShouldResumeGame()
        }
    }

    protected fun setHandPlayer(teamPlayer: TeamPlayer) {
        handPlayer = teamPlayer
        _currentHandPlayerPosition.value = TrucoPlayerPosition.get(teamPlayer, teamPlayers, myTeamPlayer)
    }

    private fun canAnswer(otherPlayer: TeamPlayer): Boolean =
        teamPlayers.last { it.team != otherPlayer.team } == myTeamPlayer

    private fun actionWasMadeByMyTeam(otherPlayer: TeamPlayer): Boolean =
        otherPlayer.team == myTeamPlayer.team

    private fun finishGame() {
        gameAlreadyFinished = true
        dispatchSingleTimeEvent(TrucoFinishGame)
    }

    /**
     * Updates currentActionPoints and currentAction.
     * currentAction value only will be recorded if the action received is not yes or no, in order to keep the history.
     */
    private fun updateActionValues(action: TrucoAction) {
        when (action) {
            is YesIDo -> {
                val lastAction = previousActions.last()
                val actionPoints = lastAction.yesPoints
                currentActionPoints = actionPoints
                updateTrucoAccumulatedPoints(lastAction, actionPoints)

            }
            is NoIDont -> currentActionPoints = previousActions.last().noPoints
            is GoToDeck -> currentActionPoints = getGoToDeckPoints()
            else -> previousActions = previousActions + action
        }
    }

    private fun updateTrucoAccumulatedPoints(action: TrucoAction, actionPoints: Int) {
        when(action) {
            is Truco, is Retruco, is ValeCuatro -> {
                _trucoAccumulatedPoints.value = actionPoints
            }
        }
    }

    private fun cleanActionValues() {
        currentActionPoints = 1
        previousActions = emptyList()
    }

    private fun disableButtonsIfApplies(action: TrucoAction, actionPerformer: Boolean) {
        when (action) {
            is ValeCuatro -> updateTrucoValues(action, buttonEnabled = false)
            is TrucoGameAction -> updateTrucoValues(action, buttonEnabled = !actionPerformer)
            is EnvidoGoesFirst -> {
                _lastTrucoAction.value = null
                _trucoButtonEnabled.value = true
                _envidoButtonEnabled.value = false
                envidoDisabledForHand = true
            }
            is EnvidoGameAction -> {
                _envidoButtonEnabled.value = false
                envidoDisabledForHand = true
            }
            is YesIDo -> if (isAcceptingTruco()) _envidoButtonEnabled.value = false
            else -> Unit
        }
    }

    private fun setEnvidoButtonAvailability(enabled: Boolean) {
        _envidoButtonEnabled.value = enabled && !envidoDisabledForHand
    }

    private fun isAcceptingTruco() = _lastTrucoAction.value != null

    private fun updateTrucoValues(action: TrucoGameAction, buttonEnabled: Boolean) {
        _lastTrucoAction.value = action
        _trucoButtonEnabled.value = buttonEnabled
        envidoDisabledForHand = true
    }

    fun setTotalPlayers(players: Int) {
        _totalPlayers.value = players
    }

    fun setTotalPoints(points: Int) {
        _totalPoints.value = points
    }

    private fun onRivalCardPlayed(playedCard: PlayedCard) {
        dispatchSingleTimeEvent(
            TrucoOtherPlayedCardEvent(
                TrucoPlayerPosition.get(playedCard.teamPlayer, teamPlayers, myTeamPlayer),
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
        if (round == 1) envidoDisabledForHand = true
        _currentRound.value = round + 1
        playedCards.add(mutableListOf())
        val roundWinnerTeamPlayer = getRoundWinnerTeamPlayer(currentRoundPlayedCards)
        val roundResult = TrucoRoundResult.get(roundWinnerTeamPlayer, myTeamPlayer)
        currentHandWinners.add(roundWinnerTeamPlayer)

        if (hasCurrentHandFinished()) {
            onHandFinished()
        } else {
            dispatchSingleTimeEvent(TrucoFinishRound(round, roundResult))
            nextTurn(roundWinnerTeamPlayer)
        }
    }

    private fun onHandFinished(handWinnerPlayerTeam: Int = getCurrentHandWinner().team) {
        val hasFinished = updateScore(handWinnerPlayerTeam)
        if (!hasFinished) {
            val nextHandIndex = (teamPlayers.indexOf(handPlayer) + 1)
            setHandPlayer(teamPlayers[nextHandIndex % totalPlayers.requireValue()])
            handOutCards()
        }
    }

    private fun updateScore(winnerTeam: Int): Boolean {
        val isMyTeamWinner = winnerTeam == myTeamPlayer.team
        val score = if (isMyTeamWinner) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
        val hasFinished = score.requireValue() >= _totalPoints.requireValue()
        dispatchSingleTimeEvent(TrucoShowEarnedPoints(isMyTeamWinner, currentActionPoints) {
            if (hasFinished) finishGame()
        })
        return hasFinished
    }

    private fun hasRoundFinished(): Boolean =
        playedCards.last().size == totalPlayers.requireValue() ||
                hasRoundFinishedBecauseAceOfSwords() ||
                hasRoundFinishedBecauseFullTeamLost()

    private fun hasRoundFinishedBecauseAceOfSwords() = playedCards.last()
        .firstOrNull { it.card == TrucoCardsChallenger.aceOfSwords }
        ?.let { it.teamPlayer in currentHandWinners || currentHandWinners.any { winner -> winner == null } }
        ?: false

    private fun hasRoundFinishedBecauseFullTeamLost(): Boolean {
        if (currentHandWinners.isEmpty()) {
            return false // If there's no hand finished yet, skip this condition
        }
        val currentRoundPlayedCards = playedCards.last()
        val teamWithRoundFinished = currentRoundPlayedCards
            .groupBy { it.teamPlayer.team }
            .values
            .firstOrNull { it.size == totalPlayers.requireValue() / 2 }
            ?.first()
            ?.teamPlayer
            ?.team
            ?: return false // If there's no a full team that finished the round, skip this condition
        val roundWinner = getRoundWinnerTeamPlayer(currentRoundPlayedCards)?.team
        val hasLostCurrentRound = roundWinner != null && roundWinner != teamWithRoundFinished
        val hasTieCurrentRound = roundWinner == null
        val hasLostAnyRound =
            currentHandWinners.any { it != null && it.team != teamWithRoundFinished }
        return (hasLostCurrentRound && (hasLostAnyRound || isLastRound())) || (hasTieCurrentRound && hasLostAnyRound)
    }

    private fun hasCurrentHandFinished(): Boolean {
        return currentHandWinners.size == MAX_HAND_ROUNDS ||
                groupCurrentHandWinners().any { it.size >= WINNER_HAND_ROUNDS_THRESHOLD } ||
                (currentHandWinners.any { it == null } && currentHandWinners.any { it != null })
    }

    private fun getRoundWinnerTeamPlayer(currentRoundPlayedCards: List<PlayedCard>): TeamPlayer? {
        val winnerCards =
            TrucoCardsChallenger.getWinnerCards(currentRoundPlayedCards.map { it.card })
        return currentRoundPlayedCards
            .filter { it.card in winnerCards }
            .takeIf { winners -> winners.all { it.teamPlayer == winners.first().teamPlayer } }
            ?.first()
            ?.teamPlayer
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
    private fun getCurrentHandWinner(): TeamPlayer =
        if (currentHandWinners.all { it == null })
            playedCards.first()
                .first().teamPlayer // The first player that played a card is the hand
        else
            getCurrentHandAbsoluteWinner() ?: currentHandWinners.filterNotNull().first()

    private fun groupCurrentHandWinners() = currentHandWinners
        .groupBy { it?.team }
        .filterKeys { it != null }
        .values

    private fun nextTurn(forceNextTurnPlayer: TeamPlayer? = null) {
        setCurrentPlayerTurn(forceNextTurnPlayer ?: getNextPlayerTurn())
    }

    private fun getNextPlayerTurn(): TeamPlayer {
        val nextIndex = teamPlayers.indexOf(currentTurnPlayer) + 1
        return teamPlayers[nextIndex % teamPlayers.size]
    }

    private fun setCurrentPlayerTurn(player: TeamPlayer) {
        currentTurnPlayer = player
        _currentTurnPlayerPosition.value = TrucoPlayerPosition.get(player, teamPlayers, myTeamPlayer)
        if (player == myTeamPlayer) {
            dispatchSingleTimeEvent(TrucoTakeTurnEvent)
            setEnvidoButtonAvailability(
                playedCards.last().any { it.teamPlayer.team == myTeamPlayer.team } || totalPlayers.requireValue() == 2
            )
        }
    }

    protected open fun onActionDone(action: TrucoAction, performedByTeam: Int) {
        when (action) {
            is NoIDont -> onNoIDont(performedByTeam)
            is YesIDo -> if (previousActions.last() is EnvidoGameAction) {
                playEnvido()
            }
            is GoToDeck -> {
                val winner = teamPlayers.first { it.team != performedByTeam }.team
                onHandFinished(winner)
            }
        }
    }

    private fun playEnvido() {
        val pointsByPlayer = cardsByPlayer.map {
            val teamPlayer = teamPlayers.first { teamPlayer -> teamPlayer.name == it.name }
            teamPlayer to EnvidoPointsCalculator.getPoints(it.cards)
        }
        val roundOrder = getRoundOrder()
        val playersWithPoints =
            roundOrder.map { player -> pointsByPlayer.first { it.first == player } }

        val winner = getWinner(pointsByPlayer, roundOrder)

        val actionsByPlayer: Map<TeamPlayer, TrucoAction?> =
            if (roundOrder.size == 2)
                EnvidoMessageCalculator.envidoMessagesFor2(playersWithPoints)
            else
                EnvidoMessageCalculator.envidoMessagesFor4(playersWithPoints)

        val actionsByPosition = actionsByPlayer.filter { it.value != null }.map { actionByPlayer ->
            val playerPosition =
                TrucoPlayerPosition.get(actionByPlayer.key, teamPlayers, myTeamPlayer)
            playerPosition to actionByPlayer.value!!
        }.toMap()

        dispatchSingleTimeEvent(TrucoShowManyActionsEvent(actionsByPosition) {
            updateScore(winner.first.team)
            currentActionPoints = 1
        })
    }

    private fun getWinner(
        pointsByPlayer: List<Pair<TeamPlayer, Int>>,
        roundOrder: List<TeamPlayer>
    ): Pair<TeamPlayer, Int> {
        val winnerPoints = pointsByPlayer.maxByOrNull { it.second }!!.second
        val playersWithMaxPoints = pointsByPlayer.filter { it.second == winnerPoints }
        val winnerName = roundOrder.first { playerWithCards ->
            playersWithMaxPoints.map { it.first.name }.contains(playerWithCards.name)
        }.name

        return pointsByPlayer.first { it.first.name == winnerName }
    }

    private fun getRoundOrder(): List<TeamPlayer> {
        val handIndex = teamPlayers.indexOf(handPlayer)
        return teamPlayers.drop(handIndex) + teamPlayers.take(handIndex)
    }


    private fun onNoIDont(performedByTeam: Int) {
        val winner = teamPlayers.first { it.team != performedByTeam }.team
        when (previousActions.last()) {
            is Truco, is Retruco, is ValeCuatro ->
                onHandFinished(winner)
            is EnvidoGameAction -> {
                updateScore(winner)
                currentActionPoints = 1
            }
        }
    }

    private fun isLastRound() = currentRound.requireValue() == MAX_HAND_ROUNDS

    private fun getGoToDeckPoints(): Int {
        val didTheRivalHaveTheChanceToCallEnvido = previousActions.isEmpty() &&
                currentRound.requireValue() == 1 &&
                playedCards.last().size < totalPlayers.requireValue() - 1
        return if (didTheRivalHaveTheChanceToCallEnvido) 2 else currentActionPoints
    }

    // unused
    override fun startGame() {
    }

    companion object {

        private const val MAX_HAND_ROUNDS = 3
        private const val WINNER_HAND_ROUNDS_THRESHOLD = 2
        private const val NEW_HAND_DELAY_TIME_MS = 2_000L
    }
}
