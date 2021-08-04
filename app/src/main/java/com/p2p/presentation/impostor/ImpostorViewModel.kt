package com.p2p.presentation.impostor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.impostor.message.ImpostorAssignWord
import com.p2p.model.impostor.message.ImpostorEndGame
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.basegame.KillGame
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.SinglePlayerOnGame

abstract class ImpostorViewModel(
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
    Game.IMPOSTOR
) {

    protected val _keyWord = MutableLiveData<String>()
    val keyWord: LiveData<String> = _keyWord

    protected val _impostor = MutableLiveData<String>()
    val impostor: LiveData<String> = _impostor

    protected val _isImpostor = MutableLiveData<Boolean>()
    val isImpostor: LiveData<Boolean> = _isImpostor

    fun createGame(word: String) {
        val impostor = selectImpostor()
        _impostor.value = impostor
        _isImpostor.value = impostor == userName
        _keyWord.value = word
        connection.write(
            ImpostorAssignWord(
                word,
                impostor
            )
        )
        closeDiscovery()
        goToPlay()
    }

    private fun selectImpostor(): String {
        val players =
            requireNotNull(otherPlayers()) { "At this instance at least one player must be connected" }
        return players.shuffled().first()
    }

    fun endGame() {
        connection.write(ImpostorEndGame())
    }

    override fun startGame() = goToPlay()

    override fun goToPlay() {
        gameAlreadyStarted = true
        super.goToPlay()
    }

    //TODO que se hace aca??
    override fun onClientConnectionLost(peerId: Long) {
        super.onClientConnectionLost(peerId)
        if (gameAlreadyStarted && connectedPlayers.size == 1) {
            dispatchErrorScreen(SinglePlayerOnGame {
                dispatchSingleTimeEvent(KillGame)
            })
        }
    }
}
