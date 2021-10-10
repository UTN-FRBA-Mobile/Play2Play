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
import com.p2p.model.truco.EmptyCard
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
import com.p2p.presentation.truco.actions.TrucoAction.Envido
import com.p2p.presentation.truco.actions.TrucoAction.EnvidoGoesFirst
import com.p2p.presentation.truco.actions.TrucoAction.FaltaEnvido
import com.p2p.presentation.truco.actions.TrucoAction.NoIDont
import com.p2p.presentation.truco.actions.TrucoAction.GoToDeck
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
    protected lateinit var teamPlayers: List<TeamPlayer>

    /** Set the quantity of players selected by the user when creating the game . */
    protected val _totalPlayers = MutableLiveData<Int>()
    val totalPlayers: LiveData<Int> = _totalPlayers

    /** Set the quantity of points selected by the user when creating the game . */
    private val _totalPoints = MutableLiveData<Int>()
    val totalPoints: LiveData<Int> = _totalPoints

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

    private val _goToDeckButtonEnabled = MutableLiveData(true)
    val goToDeckButtonEnabled: LiveData<Boolean> = _goToDeckButtonEnabled

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


    //Cards for when player goes to deck
    val emptyCards by lazy { listOf(EmptyCard, EmptyCard, EmptyCard) }

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
                disableButtonsIfApplies(
                    message.action,
                    actionPerformer = actionWasMadeByMyTeam(message.teamPlayer)
                )
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


    fun goToDeck() {
        performAction(GoToDeck(myTeamPlayer.name))
        _myCards.value = emptyCards
        _goToDeckButtonEnabled.value = false
        _envidoButtonEnabled.value = false
        _trucoButtonEnabled.value = false
    }


    fun playerGoToDeck(player: String) {
        cardsByPlayer = cardsByPlayer.map {
            if (it.name == player)
                PlayerWithCards(player, emptyCards)
            else it
        }
    }

    fun performEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, Envido(previousActions))

    fun performRealEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, RealEnvido(previousActions))

    // TODO: receive total opponent points
    fun performFaltaEnvido(isReply: Boolean = false) =
        performOrReplyAction(isReply, FaltaEnvido(theirScore.requireValue(), ourScore.requireValue(), previousActions))
    
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
        _goToDeckButtonEnabled.value = true
    }

    private fun canAnswer(otherPlayer: TeamPlayer): Boolean =
        teamPlayers.last { it.team != otherPlayer.team } == myTeamPlayer

    private fun actionWasMadeByMyTeam(otherPlayer: TeamPlayer): Boolean =
        otherPlayer.team == myTeamPlayer.team

    private fun finishGame() {
        dispatchSingleTimeEvent(TrucoFinishGame)
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

    private fun onHandFinished(handWinnerTeamPlayer: Int = getCurrentHandWinner().team) {
        val score = if (handWinnerTeamPlayer == myTeamPlayer.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
        if (score.requireValue() >= _totalPoints.requireValue()) {
            finishGame()
        } else {
            newHand()
        }
    }

    private fun updateScore(winnerTeam: Int) {
        val score = if (winnerTeam == myTeamPlayer.team) _ourScore else _theirScore
        score.value = score.requireValue() + currentActionPoints
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

    private fun getNextPlayerTurn(): TeamPlayer = getNextPlayerThatPlays(teamPlayers.indexOf(currentTurnPlayer) + 1)

    //Next player skipping players that have gone to deck
    private fun getNextPlayerThatPlays(nextIndex: Int): TeamPlayer {
        val nextPlayerTurn = teamPlayers[nextIndex % teamPlayers.size]
        return if (cardsByPlayer.first { it.name == nextPlayerTurn.name }.cards == emptyCards)
            getNextPlayerThatPlays(nextIndex + 1)
        else nextPlayerTurn
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
            is YesIDo -> if (previousActions.last() is EnvidoGameAction) {
                playEnvido()
                currentActionPoints = 1
            }
            is GoToDeck -> {
                if (allPlayersHaveGoneToDeck(performedByTeam))
                    looseRound(performedByTeam)
                else playerGoToDeck(action.player)
            }
        }
    }

    private fun allPlayersHaveGoneToDeck(performedByTeam: Int): Boolean {
        val myTeam = teamPlayers.filter { it.team == performedByTeam }.map { it.name }
        val myTeamInRound = cardsByPlayer.filter {
            myTeam.contains(it.name) && it.cards.sumBy { it.number } != 0
        }.size
        return myTeamInRound - 1 == 0
    }

    private fun looseRound(
        performedByTeam: Int,
        winner: Int = teamPlayers.first { it.team != performedByTeam }.team
    ) {
        onHandFinished(winner)
    }

    private fun playersThatHaveGoneToDeck() = cardsByPlayer.filterNot { it.cards == emptyCards } .map { it.name }

    private fun playEnvido() {
        val pointsByPlayer = cardsByPlayer.map {
            val teamPlayer = teamPlayers.first { teamPlayer -> teamPlayer.name == it.name }
            teamPlayer to EnvidoPointsCalculator.getPoints(it.cards)
        }
        val roundOrder = getRoundOrder()
        val playersWithPoints = roundOrder.map { player -> pointsByPlayer.first { it.first == player } }

        val winner = getWinner(pointsByPlayer, roundOrder)

        val actionsByPlayer: Map<TeamPlayer, TrucoAction?> =
            if (roundOrder.size == 2)
                EnvidoMessageCalculator.envidoMessagesFor2(playersWithPoints)
            else
                EnvidoMessageCalculator.envidoMessagesFor4(playersWithPoints)

        val actionsByPosition = actionsByPlayer.filter {
            it.value != null && !playersThatHaveGoneToDeck().contains(it.key.name)
        }.map { actionByPlayer ->
            val playerPosition =
                TrucoPlayerPosition.get(actionByPlayer.key, teamPlayers, myTeamPlayer)
            playerPosition to actionByPlayer.value!!
        }.toMap()

        dispatchSingleTimeEvent(TrucoShowManyActionsEvent(actionsByPosition))
        updateScore(winner.first.team)
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
        //TODO falta poner la mano cuando lo haga juan :)
        val handIndex = 0
        return teamPlayers.drop(handIndex) + teamPlayers.take(handIndex)
    }

    private fun onNoIDont(performedByTeam: Int) {
        val winner = teamPlayers.first { it.team != performedByTeam }.team
        when (previousActions.last()) {
            is Truco, is Retruco, is ValeCuatro -> {
                looseRound(performedByTeam, winner)
            }
            is Truco, is Retruco, is ValeCuatro ->
                onHandFinished(winner)
            is EnvidoGameAction -> {
                updateScore(winner)
                currentActionPoints = 1
            }
        }
    }


    companion object {

        private const val MAX_HAND_ROUNDS = 3
        private const val WINNER_HAND_ROUNDS_THRESHOLD = 2
    }
}
