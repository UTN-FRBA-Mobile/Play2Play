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
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses

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

    private val _actionAvailableResponses = MutableLiveData<TrucoActionAvailableResponses>()
    val actionAvailableResponses: LiveData<TrucoActionAvailableResponses> = _actionAvailableResponses

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
        when (conversation.lastMessage) {
            is TrucoActionMessage -> dispatchSingleTimeEvent(TrucoShowOpponentActionEvent(conversation.lastMessage.action))
        }
    }

    fun performAction(action: TrucoAction) {
        connection.write(TrucoActionMessage(action))
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
    }

    fun replyAction(action: TrucoAction) {
        _actionAvailableResponses.value = TrucoActionAvailableResponses.noActions()
        dispatchSingleTimeEvent(TrucoShowMyActionEvent(action))
    }
}
