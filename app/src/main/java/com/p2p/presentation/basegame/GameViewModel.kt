package com.p2p.presentation.basegame

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.message.ClientHandshakeMessage
import com.p2p.model.message.Message
import com.p2p.model.message.MessageReceived
import com.p2p.model.message.ServerHandshakeMessage
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

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val _players = MutableLiveData(listOf(userName))
    val players: LiveData<List<String>> = _players

    init {
        _game.value = theGame
        createOrJoin()
        startConnection() // TODO: This should be called when the creation is finished, from the Lobby
    }

    /**
     * Invoked when a message is received, we should read it and act accordingly.
     *
     * Override if needed but always call super.
     */
    @CallSuper
    open fun receiveMessage(messageReceived: MessageReceived) {
        when (val message = messageReceived.message) {
            is ClientHandshakeMessage -> {
                val actualPlayers = players.requireValue()
                if (isServer()) connection.answer(messageReceived, ServerHandshakeMessage(actualPlayers))
                _players.value = actualPlayers + message.name
            }
            is ServerHandshakeMessage -> {
                _players.value = players.requireValue() + message.players
            }
        }
    }

    /**
     * Invoked when a message was sent successfully.
     *
     * Override if needed.
     */
    open fun onSentSuccessfully(message: Message) {}

    /**
     * Invoked when there was an error sending a message.
     *
     * Default implementation retries the writing. Override if needed.
     */
    open fun onSentError(message: Message) = connection.write(message)

    fun startConnection() {
        connection = when {
            isServer() -> {
                bluetoothConnectionCreator.createServer()
            }
            else -> {
                val device = requireNotNull(connectionType.device) {
                    "A bluetooth device should be passed on the activity creation"
                }
                bluetoothConnectionCreator.createClient(device).also { client ->
                    client.onConnected { it.write(ClientHandshakeMessage(userName)) }
                }
            }
        }
    }

    fun showInstructions() = dispatchSingleTimeEvent(OpenInstructions(instructions))

    override fun onCleared() {
        super.onCleared()
        connection.close()
    }

    private fun createOrJoin() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> dispatchSingleTimeEvent(getCreationEvent())
            GameConnectionType.CLIENT -> dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    private fun isServer() = connectionType.type == GameConnectionType.SERVER

    protected abstract fun getCreationEvent(): GameEvent
}