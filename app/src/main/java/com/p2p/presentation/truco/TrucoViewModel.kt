package com.p2p.presentation.truco

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.R
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
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.EnvidoGameAction
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
    protected lateinit var teamPlayers: List<TeamPlayer>

    /** Set the quantity of players selected by the user when creating the game . */
    protected val _totalPlayers = MutableLiveData<Int>()
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

    private lateinit var currentTurnPlayer: TeamPlayer

    private var currentActionPoints: Int = 1
    protected var previousActions: List<TrucoAction> = emptyList()

    protected val myTeamPlayer: TeamPlayer by lazy { teamPlayers.first { it.name == userName } }
    private val playedCards: MutableList<MutableList<PlayedCard>> = mutableListOf(mutableListOf())
    private val currentHandWinners: MutableList<TeamPlayer?> = mutableListOf()

    init {
        _ourScore.value = 0
        _theirScore.value = 0
    }

    abstract override fun startGame()

    fun goToPlayTruco() {
        gameAlreadyStarted = true
        dispatchSingleTimeEvent(TrucoGoToPlay(totalPlayers.requireValue()))
    }

    /** This will only be used by the server */
    protected open fun handOutCards() {}

    @CallSuper
    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TrucoActionMessage -> {
                disableButtonsIfApplies(message.action, actionPerformer = myTeamDidAction(message.teamPlayer))
                updateActionValues(message.action)
                val playerPosition =
                    TrucoPlayerPosition.get(message.teamPlayer, teamPlayers, myTeamPlayer)
                dispatchSingleTimeEvent(
                    TrucoShowOpponentActionEvent(
                        message.action,
                        playerPosition,
                        canAnswer(message.teamPlayer)
                    )
                )
                onActionDone(message.action, message.teamPlayer.team)
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
        connection.write(TrucoActionMessage(action, myTeamPlayer))
        updateActionValues(action)
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
        onActionDone(action, myTeamPlayer.team)
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
        nextTurn(teamPlayers.first())
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

    private fun newHand() {
        dispatchSingleTimeEvent(TrucoNewHand)
        cleanActionValues()
        currentHandWinners.clear()
        _lastTrucoAction.value = null
        _envidoButtonEnabled.value = true
        _trucoButtonEnabled.value = true
    }

    private fun canAnswer(otherPlayer: TeamPlayer): Boolean =
        teamPlayers.last { it.team != otherPlayer.team } == myTeamPlayer

    private fun myTeamDidAction(otherPlayer: TeamPlayer): Boolean =
        otherPlayer.team == myTeamPlayer.team

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
        _currentRound.value = round + 1
        playedCards.add(mutableListOf())

        val roundWinnerPlayerTeam = getRoundWinnerPlayerTeam(currentRoundPlayedCards)
        val roundResult = TrucoRoundResult.get(roundWinnerPlayerTeam, myTeamPlayer)
        currentHandWinners.add(roundWinnerPlayerTeam)
        dispatchSingleTimeEvent(TrucoFinishRound(round, roundResult))

        if (hasCurrentHandFinished()) {
            onHandFinished()
        } else {
            nextTurn(roundWinnerPlayerTeam)
        }
    }

    private fun onHandFinished(handWinnerPlayerTeam: Int = getCurrentHandWinner().team) {
        val score = if (handWinnerPlayerTeam == myTeamPlayer.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
        newHand()
    }

    private fun hasRoundFinished(): Boolean {
        return playedCards.last().size == totalPlayers.requireValue() ||
                hasRoundFinishedBecauseAceOfSwords() ||
                hasRoundFinishedBecauseFullTeamLost()
    }

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

    private fun getRoundWinnerPlayerTeam(currentRoundPlayedCards: List<PlayedCard>): TeamPlayer? {
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
    private fun getCurrentHandWinner(): TeamPlayer {
        if (currentHandWinners.all { it == null }) {
            return playedCards.first()
                .first().teamPlayer // The first player that played a card is the hand
        }

        return getCurrentHandAbsoluteWinner() ?: currentHandWinners.filterNotNull().first()
    }

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
        if (player == myTeamPlayer) {
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
                //TODO mandar mensaje para jugar y sumar puntos
            }
        }
    }

    private fun playEnvido() {
        val pointsByPlayer = cardsByPlayer.map {
            val teamPlayer = teamPlayers.first { teamPlayer -> teamPlayer.name == it.name }
            teamPlayer to EnvidoPointsCalculator.getPoints(it.cards)
        }

        val winner = getWinner(pointsByPlayer)
        val winnerTurn = cardsByPlayer.map { it.name }.indexOf(winner.first.name)

        val actionsByPlayer: Map<TrucoPlayerPosition, TrucoAction> =
            pointsByPlayer.map { (teamPlayer, points) ->
                val playerPosition = TrucoPlayerPosition.get(teamPlayer, teamPlayers, myTeamPlayer)
                // The turn that the player has played
                val playerTurn = cardsByPlayer.map { it.name }.indexOf(teamPlayer.name)
                val action = if (playerTurn < winnerTurn || winnerTurn == 0) {
                    ShowEnvidoPoints(points)
                } else if (playerTurn == winnerTurn) {
                    ShowEnvidoPoints(points, R.string.truco_answer_envido_are_better)
                } else {
                    ShowEnvidoPoints(points, R.string.truco_answer_envido_are_good)
                }
                playerPosition to action
            }.toMap()

        dispatchSingleTimeEvent(TrucoShowManyActionsEvent(actionsByPlayer))
        onEnvidoPlayed(winner.first)
    }

    private fun getWinner(pointsByPlayer: List<Pair<TeamPlayer, Int>>): Pair<TeamPlayer, Int> {
        val winnerPoints = pointsByPlayer.maxByOrNull { it.second }!!.second
        val playersWithMaxPoints = pointsByPlayer.filter { it.second == winnerPoints }

        //TODO cardsByPlayer should be ordered by round turns
        val winnerName = cardsByPlayer.first { playerWithCards ->
            playersWithMaxPoints.map { it.first.name }.contains(playerWithCards.name)
        }.name

        return pointsByPlayer.first { it.first.name == winnerName }
    }


    private fun onEnvidoPlayed(winner: TeamPlayer) {
        val score = if (myTeamPlayer.team == winner.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
    }

    private fun onNoIDont(performedByTeam: Int) {
        when (previousActions.last()) {
            is Truco, is Retruco, is ValeCuatro, is EnvidoGameAction -> {
                val winner = teamPlayers.first { it.team != performedByTeam }.team
                onHandFinished(winner)
            }
        }
    }

    companion object {

        private const val MAX_HAND_ROUNDS = 3
        private const val WINNER_HAND_ROUNDS_THRESHOLD = 2
    }
}
