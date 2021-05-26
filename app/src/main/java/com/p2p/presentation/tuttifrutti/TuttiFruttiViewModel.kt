package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.userInfo.UserSession
import com.p2p.presentation.base.game.ConnectionType
import com.p2p.presentation.base.game.GameConnectionType
import com.p2p.presentation.base.game.GameViewModel

class TuttiFruttiViewModel(
    private val connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator
) : GameViewModel(connectionType, userSession, bluetoothConnectionCreator) {

    fun onStart() {
        if (connectionType.type == GameConnectionType.SERVER) {
            dispatchSingleTimeEvent(GoToSelectCategories)
        } else {
            onCreateOrJoin()
        }
    }

    companion object {
        const val UNKNOWN = "Desconocido"
    }
}