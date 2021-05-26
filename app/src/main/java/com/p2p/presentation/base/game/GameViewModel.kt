package com.p2p.presentation.base.game

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.bluetooth.basemessage.HandshakeMessage
import com.p2p.presentation.base.BaseViewModel

abstract class GameViewModel(
    private val connectionType: ConnectionType,
    private val userSession: UserSession,
    private val bluetoothConnectionCreator: BluetoothConnectionCreator
) : BaseViewModel<AbstractGameCreationEvent>() {

    fun startConnection() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> bluetoothConnectionCreator.createServer()
            GameConnectionType.CLIENT -> {
                val device = requireNotNull(connectionType.device) {
                    "A bluetooth device should be passed on the activity creation"
                }
                bluetoothConnectionCreator.createClient(device)
                    .onConnected { // TODO: should wait until connected to continue any processing
                        it.write(HandshakeMessage(userSession.getUserName() ?: UNKNOWN))
                    }
            }
        }
    }

    fun onCreateOrJoin() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> dispatchSingleTimeEvent(GoToServerLobby)
            GameConnectionType.CLIENT -> dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    companion object {
        const val UNKNOWN = "Desconocido"
    }
}