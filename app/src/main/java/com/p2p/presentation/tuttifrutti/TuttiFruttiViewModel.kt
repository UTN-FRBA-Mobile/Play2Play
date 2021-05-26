package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.bluetooth.basemessage.HandshakeMessage
import com.p2p.presentation.base.game.ConnectionType
import com.p2p.presentation.base.game.GameConnectionType
import com.p2p.presentation.base.game.GameViewModel
import com.p2p.utils.Logger

class TuttiFruttiViewModel(
    private val connectionType: ConnectionType,
    private val userSession: UserSession,
    private val bluetoothConnectionCreator: BluetoothConnectionCreator
) : GameViewModel<TuttiFruttiCreationEvent>() {

    override fun onStart() {
        Logger.d(TAG, "Starting viewModel")
        Logger.d(TAG, "Connection Type: $connectionType")
        if (connectionType.type == GameConnectionType.SERVER) {
            Logger.d(TAG,"Dispatching")
            dispatchSingleTimeEvent(GoToSelectCategories)
        } else {
            onCreateOrJoin()
        }
    }

    override fun onCreateOrJoin() {
        when (connectionType.type) {
            GameConnectionType.SERVER -> {
                bluetoothConnectionCreator.createServer()
                dispatchSingleTimeEvent(GoToServerLobby)
            }
            GameConnectionType.CLIENT -> {
                val device = requireNotNull(connectionType.device) {
                    "A bluetooth device should be passed on the activity creation"
                }
                bluetoothConnectionCreator.createClient(device)
                    .onConnected { // TODO: should wait until connected to continue any processing
                        it.write(
                            HandshakeMessage(userSession.getUserName() ?: UNKNOWN)
                        )
                    }
                dispatchSingleTimeEvent(GoToClientLobby)
            }
        }
    }

    companion object {
        const val UNKNOWN = "Desconocido"
        const val TAG = "P2P_TUTTIFRUTTI_VIEWMODEL"
    }
}