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
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses
import com.p2p.model.truco.TrucoFinalScore
import com.p2p.presentation.extensions.requireValue

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
    protected val _totalPlayers = MutableLiveData<Int>()
    val totalPlayers: LiveData<Int> = _totalPlayers

    /** Current cards for this player */
    protected val _currentCards = MutableLiveData<List<Card>>()
    val currentCards: LiveData<List<Card>> = _currentCards

    private val _actionAvailableResponses = MutableLiveData<TrucoActionAvailableResponses>()
    val actionAvailableResponses: LiveData<TrucoActionAvailableResponses> = _actionAvailableResponses

    // TODO: When setting this values, order them in a descendant order of scores
    private val _finalScores = MutableLiveData<List<TrucoFinalScore>>()
    val finalScores: LiveData<List<TrucoFinalScore>> = _finalScores

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
        when (conversation.lastMessage) {
            // TODO: Implement messages handling
        }
    }

    fun performAction(action: TrucoAction) {
        // TODO: perform action
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
    }

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
    }

    fun isPlayerInWinnerTeam(trucoFinalScores: List<TrucoFinalScore>): Boolean {
        return userName in trucoFinalScores.first().players
    }

    fun getPlayerTeam(trucoFinalScores: List<TrucoFinalScore>) : Int {
        return trucoFinalScores.first { it.players.contains(userName) }.team
    }
}
