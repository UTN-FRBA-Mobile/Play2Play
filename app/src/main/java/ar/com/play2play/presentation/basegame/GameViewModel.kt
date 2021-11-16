package ar.com.play2play.presentation.basegame

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ar.com.play2play.R
import ar.com.play2play.data.bluetooth.BluetoothConnection
import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.bluetooth.BluetoothServerConnection
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.HiddenLoadingScreen
import ar.com.play2play.model.LoadingScreen
import ar.com.play2play.model.VisibleLoadingScreen
import ar.com.play2play.model.base.message.*
import ar.com.play2play.presentation.base.BaseViewModel
import ar.com.play2play.presentation.extensions.requireValue
import ar.com.play2play.presentation.home.games.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class GameViewModel(
    private val connectionType: ConnectionType,
    private val userSession: UserSession,
    private val bluetoothConnectionCreator: BluetoothConnectionCreator,
    private val instructionsRepository: InstructionsRepository,
    protected val loadingTextRepository: LoadingTextRepository,
    val theGame: Game
) : BaseViewModel<GameEvent>() {

    protected open val maxPlayersOnRoom: Int = Int.MAX_VALUE
    protected open val playersRecoverability = PlayersRecoverability.CANNOT_RECOVER

    protected var gameAlreadyStarted = false
        set(value) {
            field = value
            updateIsBackButtonVisible()
        }
    protected var gameAlreadyFinished = false
        set(value) {
            field = value
            updateIsBackButtonVisible()
        }

    protected val userName by lazy { userSession.getUserNameOrEmpty() }
    private val instructions by lazy { instructionsRepository.getInstructions(game.requireValue()) }
    private val failingMessagesRetries = mutableMapOf<Message, Int>()

    private var _connection: BluetoothConnection? = null
    protected val connection: BluetoothConnection
        get() = requireNotNull(_connection) { "The connection was not initialized yet." }

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
    private var lostPlayers = emptySet<String>()
        set(value) {
            field = value
            if (playersRecoverability.shouldPauseGame(lostPlayers)) {
                dispatchSingleTimeEvent(PauseGame(value.toList()))
            }
        }

    private val _myDeviceName = MutableLiveData<String>()
    val myDeviceName: LiveData<String> = _myDeviceName

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> = _game

    protected val _players = MutableLiveData(emptyList<String>())
    val players: LiveData<List<String>> = _players

    //Loading value for loading screen, being first if isLoading and second the text to show
    private val _loadingScreen = MutableLiveData<LoadingScreen>()
    val loadingScreen: LiveData<LoadingScreen> = _loadingScreen

    private val _error = MutableLiveData<GameError?>()
    val error: LiveData<GameError?> = _error

    private val _isBackButtonVisible = MutableLiveData<Boolean>()
    val isBackAllowed: LiveData<Boolean> = _isBackButtonVisible

    init {
        _game.value = theGame
        _myDeviceName.value = bluetoothConnectionCreator.getMyDeviceName()
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
            is ClientHandshakeMessage -> onClientHandshake(message, conversation)
            is NameInUseMessage -> dispatchErrorScreen(NameInUseError {
                dispatchSingleTimeEvent(KillGame)
            })
            is RoomIsAlreadyFullMessage -> dispatchErrorScreen(RoomIsAlreadyFullError {
                dispatchSingleTimeEvent(KillGame)
            })
            is RejoinNameErrorMessage -> dispatchErrorScreen(RejoinNameError(message.availableNames) {
                dispatchSingleTimeEvent(KillGame)
            })
            is WrongGameJoinedMessage -> dispatchErrorScreen(WrongJoinedGameError {
                dispatchSingleTimeEvent(KillGame)
            })
            is ServerHandshakeMessage -> {
                connectedPlayers = message.players.map { conversation.peer to it }
                lostPlayers = lostPlayers
                    .filterNot { lost -> connectedPlayers.any { (_, connected) -> lost == connected } }
                    .toSet()
            }
            is GoodbyePlayerMessage -> {
                connectedPlayers.firstOrNull { it.second == message.name }?.let { removePlayer(it) }
            }
            is PauseGameMessage -> {
                lostPlayers = message.lostPlayers.toSet()
            }
        }
    }

    /**
     * Invoked when a message was sent successfully.
     *
     * Override if needed.
     */
    @CallSuper
    open fun onSentSuccessfully(conversation: Conversation) {
        failingMessagesRetries.remove(conversation.lastMessage)
    }

    /**
     * Invoked when there was an error sending a message.
     *
     * Default implementation retries the writing [RETRY_MESSAGE_ERROR_COUNT] times. Override if needed.
     */
    open fun onSentError(message: Message) {
        val failingMessageRetries = failingMessagesRetries.getOrElse(message) { 0 }
        if (failingMessageRetries < RETRY_MESSAGE_ERROR_COUNT) {
            failingMessagesRetries[message] = failingMessageRetries + 1
            viewModelScope.launch(Dispatchers.Default) {
                delay(RETRY_MESSAGE_ERROR_DELAY * (failingMessageRetries + 1))
                connection.write(message)
            }
        } else {
            dispatchErrorScreen(ServerConnectionLostError {
                dispatchSingleTimeEvent(KillGame)
            })
        }
    }

    /** Invoked when the client connection to the server was established successfully. */
    @CallSuper
    open fun onClientConnectionSuccess() =
        connection.write(ClientHandshakeMessage(userName, theGame.id))

    /** Invoked when there was an error while trying to connect the client with the server. */
    @CallSuper
    open fun onClientConnectionFailure() =
        dispatchErrorScreen(CannotEstablishClientConnectionError {
            clearError()
            startConnection()
        })

    /** Invoked when the connection with the given [peerId] was lost. */
    @CallSuper
    open fun onClientConnectionLost(peerId: Long) {
        if (gameAlreadyFinished) return
        val playerLost = connectedPlayers.firstOrNull { it.first == peerId } ?: return
        if (gameAlreadyStarted) {
            lostPlayers = lostPlayers + playerLost.second
            connection.write(playersRecoverability.constructOnPlayerLostMessage(lostPlayers))
        }
        removePlayer(playerLost)
    }

    @CallSuper
    open fun onServerConnectionLost() {
        if (gameAlreadyFinished) return
        dispatchErrorScreen(ServerConnectionLostError {
            dispatchSingleTimeEvent(KillGame)
        })
    }

    fun startConnection() {
        _connection = if (isServer()) {
            bluetoothConnectionCreator.makeMeVisible()
            bluetoothConnectionCreator.createServer()
        } else {
            bluetoothConnectionCreator.createClient(requireNotNull(connectionType.device) {
                "A bluetooth device should be passed on the activity creation"
            })
        }
    }

    fun makeMeVisible() = bluetoothConnectionCreator.makeMeVisible()

    fun showInstructions() = dispatchSingleTimeEvent(OpenInstructions(instructions))

    fun goToLobby() = dispatchSingleTimeEvent(if (isServer()) GoToServerLobby else GoToClientLobby)

    abstract fun startGame()

    @CallSuper
    open fun goToPlay() = dispatchSingleTimeEvent(GoToPlay)

    fun closeDiscovery() {
        _connection?.let { if (it is BluetoothServerConnection) it.stopAccepting() }
    }

    override fun onCleared() {
        _connection?.close()
    }

    fun isServer() = connectionType.type == GameConnectionType.SERVER

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

    fun getOtherPlayers(): List<String>? =
        players.value?.let { it - userName }

    private fun removePlayer(playerLost: Pair<Long, String>) {
        connectedPlayers = connectedPlayers - playerLost
        dispatchMessage(
            textRes = R.string.error_client_connection_lost,
            type = MessageData.Type.ERROR,
            formatArgs = arrayOf(playerLost.second),
        )
    }

    /**
     * Invoked when a client has joined to the room.
     * Returns true if the client has joined successfully, false if there was an error.
     */
    @CallSuper
    protected open fun onClientHandshake(
        message: ClientHandshakeMessage,
        conversation: Conversation
    ): Boolean {
        if (isServer()) {
            return when {
                connectedPlayers.any { it.second == message.name } -> {
                    connection.talk(conversation, NameInUseMessage())
                    false
                }
                connectedPlayers.size >= maxPlayersOnRoom -> {
                    connection.talk(conversation, RoomIsAlreadyFullMessage())
                    false
                }
                message.joinedGame != theGame.id -> {
                    connection.talk(conversation, WrongGameJoinedMessage())
                    false
                }
                gameAlreadyStarted && !playersRecoverability.canJoinToStartedGame(
                    lostPlayers,
                    message.name
                ) -> {
                    connection.talk(
                        conversation,
                        playersRecoverability.constructCantJoinToStartedGameMessage(lostPlayers)
                    )
                    false
                }
                else -> {
                    connectedPlayers = connectedPlayers + (conversation.peer to message.name)
                    lostPlayers = lostPlayers - message.name
                    connection.write(ServerHandshakeMessage(connectedPlayers.map { it.second }))
                    true
                }
            }
        }
        return false
    }

    protected fun checkIfShouldResumeGame() {
        if (playersRecoverability.shouldResumeGame(lostPlayers)) {
            dispatchSingleTimeEvent(ResumeGame)
        }
    }

    private fun updateIsBackButtonVisible() {
        _isBackButtonVisible.value = !gameAlreadyStarted || gameAlreadyFinished
    }

    companion object {

        const val MYSELF_PEER_ID = -1L
        private const val RETRY_MESSAGE_ERROR_COUNT = 3
        private const val RETRY_MESSAGE_ERROR_DELAY = 1_000L
    }
}

