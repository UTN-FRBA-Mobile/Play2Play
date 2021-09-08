package com.p2p.presentation.impostor

import androidx.lifecycle.ViewModel
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.basegame.GameConnectionType
import com.p2p.presentation.basegame.GameViewModelFactory

class ImpostorViewModelFactory(
    activity: GameActivity<*, *>,
    data: Data
) : GameViewModelFactory(activity, data) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == ImpostorViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            if (data.gameConnectionType == GameConnectionType.SERVER) {
                super.create(ServerImpostorViewModel::class.java) as T
            } else {
                super.create(ClientImpostorViewModel::class.java) as T
            }
        } else {
            throw IllegalStateException("ImpostorViewModelFactory only creates impostor view models")
        }
    }
}
