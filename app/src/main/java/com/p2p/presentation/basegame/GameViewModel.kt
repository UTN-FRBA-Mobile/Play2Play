package com.p2p.presentation.basegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.bluetooth.Message
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.bluetooth.basemessage.HandshakeMessage
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

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val instructions by lazy { instructionsRepository.getInstructions(game.requireValue()) }
    private lateinit var connection: BluetoothConnection

    init {
        _game.value = theGame
        createOrJoin()
        startConnection() // TODO: This should be called when the creation is finished, from the Lobby
    }

    /**
     * Invoked when a message arrived, we should read it and act accordingly.
     *
     * Override if needed.
     */
    open fun readMessage(message: Message) {}

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
        connection = when (connectionType.type) {
            GameConnectionType.SERVER -> bluetoothConnectionCreator.createServer()
            else -> {
                val device = requireNotNull(connectionType.device) {
                    "A bluetooth device should be passed on the activity creation"
                }
                bluetoothConnectionCreator.createClient(device).also { client ->
                    client.onConnected {
                        it.write(HandshakeMessage(userSession.getUserNameOrEmpty()))
                    }
                }
            }
        }
    }

    fun showInstructions() = dispatchSingleTimeEvent(OpenInstructions(instructions))

    private fun createOrJoin() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> dispatchSingleTimeEvent(getCreationEvent())
            GameConnectionType.CLIENT -> dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    protected abstract fun getCreationEvent(): GameEvent
}