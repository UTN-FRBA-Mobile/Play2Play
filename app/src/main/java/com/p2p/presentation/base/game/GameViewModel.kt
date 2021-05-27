package com.p2p.presentation.base.game

import com.p2p.R
import com.p2p.data.bluetooth.BluetoothConnection
import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.bluetooth.basemessage.HandshakeMessage
import com.p2p.presentation.base.BaseViewModel

abstract class GameViewModel(
    private val connectionType: ConnectionType,
    private val userSession: UserSession,
    private val bluetoothConnectionCreator: BluetoothConnectionCreator
) : BaseViewModel<GameEvent>() {

    lateinit var connection: BluetoothConnection

    fun startConnection() {
        connection = when (connectionType.type) {
            GameConnectionType.SERVER -> bluetoothConnectionCreator.createServer()
            else -> {
                val device = requireNotNull(connectionType.device) {
                    "A bluetooth device should be passed on the activity creation"
                }
                bluetoothConnectionCreator.createClient(device).also { client ->
                    client.onConnected { // TODO: should wait until connected to continue any processing
                        it.write(HandshakeMessage(userSession.getUserName() ?: R.string.unknown.toString()))
                    }
                }
            }
        }
    }

    fun createOrJoin() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> dispatchSingleTimeEvent(creationEvent())
            GameConnectionType.CLIENT -> dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    protected abstract fun creationEvent(): GameEvent
}