package com.p2p.presentation.truco

import androidx.lifecycle.ViewModel
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.basegame.GameConnectionType
import com.p2p.presentation.basegame.GameViewModelFactory

class TrucoViewModelFactory(
    activity: GameActivity<*, *>,
    data: Data
) : GameViewModelFactory(activity, data) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == TrucoViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            if (data.gameConnectionType == GameConnectionType.SERVER) {
                super.create(ServerTrucoViewModel::class.java) as T
            } else {
                super.create(ClientTrucoViewModel::class.java) as T
            }
        } else {
            throw IllegalStateException("TrucoViewModelFactory only creates truco view models")
        }
    }
}
