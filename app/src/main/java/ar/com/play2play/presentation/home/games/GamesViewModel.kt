package ar.com.play2play.presentation.home.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.R
import ar.com.play2play.data.bluetooth.BluetoothStateProvider
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.presentation.base.BaseViewModel

class GamesViewModel(
    private val userSession: UserSession,
    private val bluetoothStateProvider: BluetoothStateProvider
) : BaseViewModel<GamesEvents>() {

    /** The list of games available to play. */
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    /** The current saved user name. */
    private val _userName = MutableLiveData(userSession.getUserName())
    val userName: LiveData<String?> = _userName

    private var turnOnBluetoothOnResult: Pair<Game, TurnOnBluetoothReason>? = null

    init {
        _games.value = Game.values().toList()
    }

    /** Open the view that corresponds to create the [game]. */
    fun createGame(game: Game, userName: String?) {
        when {
            !validateAndSaveName(userName) -> return
            !bluetoothStateProvider.isEnabled() -> {
                turnOnBluetoothOnResult = game to TurnOnBluetoothReason.CREATE
                dispatchSingleTimeEvent(TurnOnBluetooth)
            }
            else -> when (game) {
                Game.TUTTI_FRUTTI -> dispatchSingleTimeEvent(GoToCreateTuttiFrutti)
                Game.IMPOSTOR -> dispatchSingleTimeEvent(GoToCreateImpostor)
                Game.TRUCO -> dispatchSingleTimeEvent(GoToCreateTruco)
            }
        }
    }

    /** Open the view to join to a game. */
    fun joinGame(game: Game, userName: String?) {
        when {
            !validateAndSaveName(userName) -> return
            !bluetoothStateProvider.isEnabled() -> {
                turnOnBluetoothOnResult = game to TurnOnBluetoothReason.JOIN
                dispatchSingleTimeEvent(TurnOnBluetooth)
            }
            else -> dispatchSingleTimeEvent(JoinGame(game))
        }
    }

    fun onBluetoothTurnedOn(userName: String?) {
         val (game, reason) = turnOnBluetoothOnResult ?: return
        when (reason) {
            TurnOnBluetoothReason.JOIN -> joinGame(game, userName)
            TurnOnBluetoothReason.CREATE -> createGame(game, userName)
        }
    }

    private fun validateAndSaveName(name: String?) = when {
        name.isNullOrBlank() -> {
            dispatchMessage(
                textRes = R.string.games_name_error,
                type = MessageData.Type.ERROR
            )
            false
        }
        name.length > NAME_MAX_LENGTH -> {
            dispatchMessage(
                textRes = R.string.games_name_max_length_error,
                type = MessageData.Type.ERROR
            )

            false
        }
        else -> {
            saveName(name)
            true
        }
    }

    private fun saveName(name: String) {
        _userName.value = name
        userSession.saveUserName(name)
    }

    private enum class TurnOnBluetoothReason {
        JOIN,
        CREATE
    }

    companion object {

        private const val NAME_MAX_LENGTH = 7
    }
}
