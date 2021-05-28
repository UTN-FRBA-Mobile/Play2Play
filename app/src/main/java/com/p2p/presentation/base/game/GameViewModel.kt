package com.p2p.presentation.base.game

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

    init {
        createOrJoin()
        startConnection() // TODO: This should be called when the creation is finished, from the Lobby
    }

    fun startConnection() {
        connection = when (connectionType.type) {
            GameConnectionType.SERVER -> bluetoothConnectionCreator.createServer()
            else -> {
                val device = requireNotNull(connectionType.device) {
                    "A bluetooth device should be passed on the activity creation"
                }
                bluetoothConnectionCreator.createClient(device).also { client ->
                    client.onConnected {
                        it.write(HandshakeMessage(userSession.getUserNameOrEmpty()))
                    }
                }
            }
        }
    }

    fun createOrJoin() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> dispatchSingleTimeEvent(getCreationEvent())
            GameConnectionType.CLIENT -> dispatchSingleTimeEvent(GoToClientLobby)
        }
    }

    protected abstract fun getCreationEvent(): GameEvent
}