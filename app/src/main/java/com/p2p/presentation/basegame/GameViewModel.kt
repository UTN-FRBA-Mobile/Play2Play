package com.p2p.presentation.basegame

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.ClientHandshakeMessage
import com.p2p.model.base.message.ConversationMessage
import com.p2p.model.base.message.Message
import com.p2p.model.base.message.ServerHandshakeMessage
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game

abstract class GameViewModel(
    private val connectionType: ConnectionType,
    private val userSession: UserSession,
    private val bluetoothConnectionCreator: BluetoothConnectionCreator,
    private val instructionsRepository: InstructionsRepository,
    theGame: Game
) : BaseViewModel<GameEvent>() {

    private val userName by lazy { userSession.getUserNameOrEmpty() }
    private val instructions by lazy { instructionsRepository.getInstructions(game.requireValue()) }

    protected lateinit var connection: BluetoothConnection
    protected var connectedPlayers = emptyList<Pair<Long, String>>()
        set(value) {
            field = value
            _players.value = value.map { it.second }
        }

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val _players = MutableLiveData(emptyList<String>())
    val players: LiveData<List<String>> = _players

    init {
        _game.value = theGame
        connectedPlayers = listOf(MYSELF_ID to userName)
        createOrJoin()
        startConnection() // TODO: This should be called when the creation is finished, from the Lobby
    }

    /**
     * Invoked when a message is received, we should read it and act accordingly.
     *
     * Override if needed but always call super.
     */
    @CallSuper
    open fun receiveMessage(conversationMessage: ConversationMessage) {
        when (val message = conversationMessage.message) {
            is ClientHandshakeMessage -> {
                if (isServer()) {
                    connection.talk(conversationMessage, ServerHandshakeMessage(connectedPlayers.map { it.second }))
                }
                connectedPlayers = connectedPlayers + (conversationMessage.peer to message.name)
            }
            is ServerHandshakeMessage -> {
                connectedPlayers = connectedPlayers + message.players.map { 0L to it }
            }
        }
    }

    /**
     * Invoked when a message was sent successfully.
     *
     * Override if needed.
     */
    open fun onSentSuccessfully(conversationMessage: ConversationMessage) {}

    /**
     * Invoked when there was an error sending a message.
     *
     * Default implementation retries the writing. Override if needed.
     */
    open fun onSentError(message: Message) = connection.write(message)

    fun startConnection() {
        connection = if (isServer()) {
            bluetoothConnectionCreator.createServer()
        } else {
            val device = requireNotNull(connectionType.device) {
                "A bluetooth device should be passed on the activity creation"
            }
            bluetoothConnectionCreator.createClient(device).also { client ->
                client.onConnected { it.write(ClientHandshakeMessage(userName)) }
            }
        }
    }

    fun showInstructions() = dispatchSingleTimeEvent(OpenInstructions(instructions))

    fun goToPlay() = dispatchSingleTimeEvent(GoToPlay)

    override fun onCleared() {
        connection.close()
    }

    protected fun isServer() = connectionType.type == GameConnectionType.SERVER

    private fun createOrJoin() {
        if (isServer()) {
            dispatchSingleTimeEvent(GoToCreate)
        } else {
            dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    companion object {

        const val MYSELF_ID = -1L
    }
}
