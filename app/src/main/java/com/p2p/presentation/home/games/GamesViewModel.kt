package com.p2p.presentation.home.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.R
import com.p2p.data.bluetooth.BluetoothStateProvider
import com.p2p.data.userInfo.UserSession
import com.p2p.presentation.base.BaseViewModel

class GamesViewModel(
    private val userSession: UserSession,
    private val bluetoothStateProvider: BluetoothStateProvider
) : BaseViewModel<GamesEvents>() {

    /** The list of games available to play. */
    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    /** Whether the create button is enabled or not. */
    private val _createButtonEnabled = MutableLiveData<Boolean>()
    val createButtonEnabled: LiveData<Boolean> = _createButtonEnabled

    /** The current saved user name. */
    private val _userName = MutableLiveData(userSession.getUserName())
    val userName: LiveData<String?> = _userName

    private var selectedGame: Game? = null

    init {
        _games.value = Game.values().toList()
    }

    /** Select the given [game] and allow to create it. */
    fun selectGame(game: Game?) {
        _createButtonEnabled.value = game != null
        selectedGame = game
    }

    /** Open the view that corresponds to create the [selectedGame]. */
    fun createGame(userName: String?) {
        when {
            !validateAndSaveName(userName) -> return
            !bluetoothStateProvider.isEnabled() -> dispatchSingleTimeEvent(TurnOnBluetooth)
            else -> when (selectedGame) {
                Game.TUTTI_FRUTTI -> dispatchSingleTimeEvent(GoToCreateTuttiFrutti)
            }
        }
    }

    /** Open the view to join to a game. */
    fun joinGame(userName: String?) {
        when {
            !validateAndSaveName(userName) -> return
            !bluetoothStateProvider.isEnabled() -> dispatchSingleTimeEvent(TurnOnBluetooth)
            else -> dispatchSingleTimeEvent(JoinGame)
        }
    }

    private fun validateAndSaveName(name: String?): Boolean {
        return if (name.isNullOrBlank()) {
            dispatchMessage(
                textRes = R.string.games_name_error,
                type = MessageData.Type.ERROR
            )
            false
        } else {
            saveName(name)
            true
        }
    }

    private fun saveName(name: String) {
        _userName.value = name
        userSession.saveUserName(name)
    }
}
