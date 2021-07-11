package com.p2p.presentation.impostor

import androidx.lifecycle.viewModelScope
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.model.tuttifrutti.message.FinalScoreMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartGameMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartRoundMessage
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.basegame.KillGame
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.tuttifrutti.create.categories.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServerImpostorViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : ImpostorViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository
) {

    override fun startGame(keyWord: String) {
        //TODO bren definir palabras
        connection.write(
            //TODO replace
            TuttiFruttiStartGameMessage(
                emptyList(),
                emptyList()
            )
        )
        closeDiscovery()
    }

}
