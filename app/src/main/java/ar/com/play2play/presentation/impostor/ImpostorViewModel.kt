package ar.com.play2play.presentation.impostor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.impostor.ImpostorData
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.impostor.message.ImpostorAssignWord
import ar.com.play2play.model.impostor.message.ImpostorEndGame
import ar.com.play2play.presentation.basegame.ConnectionType
import ar.com.play2play.presentation.basegame.GameViewModel
import ar.com.play2play.presentation.basegame.KillGame
import ar.com.play2play.presentation.home.games.Game
import ar.com.play2play.presentation.tuttifrutti.SinglePlayerOnGame

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