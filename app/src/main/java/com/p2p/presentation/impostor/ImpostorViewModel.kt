package com.p2p.presentation.impostor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.impostor.ImpostorData
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

    protected val _impostorData = MutableLiveData<ImpostorData>()
    val impostorData: LiveData<ImpostorData> = _impostorData

    fun createGame(word: String, wordTheme: String) {
        val impostor = selectImpostor()
        // Creator is never the impostor. TODO: DO check this if we set default words/themes.
        _impostorData.value = ImpostorData(impostor, word, wordTheme, isImpostor = false)
        connection.write(
            ImpostorAssignWord(
                word,
                wordTheme,
                impostor
            )
        )
        closeDiscovery()
        goToPlay()
    }

    private fun selectImpostor(): String {
        val players =
            requireNotNull(getOtherPlayers()) { "At this instance at least one player must be connected" }
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
}