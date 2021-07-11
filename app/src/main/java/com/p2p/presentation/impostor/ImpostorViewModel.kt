package com.p2p.presentation.impostor

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo
import com.p2p.model.tuttifrutti.RoundInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.GameViewModel
import com.p2p.presentation.basegame.KillGame
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.SinglePlayerOnGame
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.presentation.tuttifrutti.finalscore.TuttiFruttiFinalScore

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

    abstract fun startGame(keyWord: String)

    override fun goToPlay() {
        gameAlreadyStarted = true
        super.goToPlay()
    }


    //TODO bren ver que se hace
    override fun onClientConnectionLost(peerId: Long) {
        super.onClientConnectionLost(peerId)
        if (gameAlreadyStarted && connectedPlayers.size == 1) {
            dispatchErrorScreen(SinglePlayerOnGame {
                dispatchSingleTimeEvent(KillGame)
            })
        }
    }
}
