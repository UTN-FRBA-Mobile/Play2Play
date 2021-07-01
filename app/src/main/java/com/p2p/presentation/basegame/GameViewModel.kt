package com.p2p.presentation.basegame

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.R
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.bluetooth.BluetoothServer
import com.p2p.model.HiddenLoadingScreen
import com.p2p.model.LoadingScreen
import com.p2p.model.VisibleLoadingScreen
import com.p2p.model.base.message.ClientHandshakeMessage
import com.p2p.model.base.message.Conversation
import com.p2p.model.base.message.GoodbyePlayerMessage
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
    protected val loadingTextRepository: LoadingTextRepository,
    theGame: Game
) : BaseViewModel<GameEvent>() {

    private val userName by lazy { userSession.getUserNameOrEmpty() }
    private val instructions by lazy { instructionsRepository.getInstructions(game.requireValue()) }

    protected lateinit var connection: BluetoothConnection

    /**
     * Contains all the connected players recognizing them by a [Long] id.
     *
     * The server will have different ids for each client, but the clients will have all players
     * with the same id (the server id). Because of that the client should never try to recognize a player by their id.
     */
    protected var connectedPlayers = emptyList<Pair<Long, String>>()
        set(value) {
            field = value
            _players.value = value.map { it.second }
        }

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    private val _players = MutableLiveData(emptyList<String>())
    val players: LiveData<List<String>> = _players

    //Loading value for loading screen, being first if isLoading and second the text to show
    private val _loadingScreen = MutableLiveData<LoadingScreen>()
    val loadingScreen: LiveData<LoadingScreen> = _loadingScreen

    private val _error = MutableLiveData<GameError?>()
    val error: LiveData<GameError?> = _error

    init {
        _game.value = theGame
        connectedPlayers = listOf(MYSELF_PEER_ID to userName)
        createOrJoin()
    }

    /**
     * Invoked when a message is received, we should read it and act accordingly.
     *
     * Override if needed but always call super.
     */
    @CallSuper
    open fun receiveMessage(conversation: Conversation) {
        when (val message = conversation.lastMessage) {
            is ClientHandshakeMessage -> {
                if (isServer()) {
                    connection.talk(
                        conversation,
                        ServerHandshakeMessage(connectedPlayers.map { it.second })
                    )
                }
                connectedPlayers = connectedPlayers + (conversation.peer to message.name)
            }
            is ServerHandshakeMessage -> {
                connectedPlayers =
                    connectedPlayers + message.players.map { conversation.peer to it }
            }
            is GoodbyePlayerMessage -> {
                removePlayer(connectedPlayers.first { it.second == message.name })
            }
        }
    }

    /**
     * Invoked when a message was sent successfully.
     *
     * Override if needed.
     */
    open fun onSentSuccessfully(conversation: Conversation) {}

    /**
     * Invoked when there was an error sending a message.
     *
     * Default implementation retries the writing. Override if needed.
     */
    open fun onSentError(message: Message) = connection.write(message)

    /** Invoked when the client connection to the server was established successfully. */
    @CallSuper
    open fun onClientConnectionSuccess() = connection.write(ClientHandshakeMessage(userName))

    /** Invoked when there was an error while trying to connect the client with the server. */
    @CallSuper
    open fun onClientConnectionFailure() = dispatchErrorScreen(CannotEstablishClientConnectionError {
        clearError()
        startConnection()
    })

    /** Invoked when the connection with the given [peerId] was lost. */
    @CallSuper
    open fun onClientConnectionLost(peerId: Long) {
        val playerLost = connectedPlayers.first { it.first == peerId }
        connection.write(GoodbyePlayerMessage(playerLost.second))
        removePlayer(playerLost)
    }

    fun startConnection() {
        connection = if (isServer()) {
            bluetoothConnectionCreator.createServer()
        } else {
            bluetoothConnectionCreator.createClient(requireNotNull(connectionType.device) {
                "A bluetooth device should be passed on the activity creation"
            })
        }
    }

    fun showInstructions() = dispatchSingleTimeEvent(OpenInstructions(instructions))

    fun goToLobby() = dispatchSingleTimeEvent(if (isServer()) GoToServerLobby else GoToClientLobby)

    fun goToPlay() = dispatchSingleTimeEvent(GoToPlay)

    fun closeDiscovery() {
        when (connection) {
            is BluetoothServer -> (connection as BluetoothServer).stopAccepting() //TODO prettify
            else -> Unit
        }
    }

    override fun onCleared() {
        connection.close()
    }

    protected fun isServer() = connectionType.type == GameConnectionType.SERVER

    protected fun getPlayerById(playerId: Long) =
        connectedPlayers.first { it.first == playerId }.second

    protected fun dispatchErrorScreen(error: GameError) {
        _error.value = error
    }

    protected fun clearError() {
        _error.value = null
    }

    protected fun startLoading(loadingMessage: String) {
        _loadingScreen.value = VisibleLoadingScreen(loadingMessage)
    }

    fun stopLoading() {
        _loadingScreen.value = HiddenLoadingScreen
    }

    private fun createOrJoin() {
        if (isServer()) {
            dispatchSingleTimeEvent(GoToCreate)
        } else {
            dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    private fun removePlayer(playerLost: Pair<Long, String>) {
        connectedPlayers = connectedPlayers - playerLost
        dispatchMessage(
            textRes = R.string.error_client_connection_lost,
            type = MessageData.Type.ERROR,
            formatArgs = arrayOf(playerLost.second),
        )
    }

    companion object {

        const val MYSELF_PEER_ID = -1L
    }
}
