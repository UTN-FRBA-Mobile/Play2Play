package com.p2p.presentation.tuttifrutti

import androidx.lifecycle.ViewModel
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.basegame.GameConnectionType
import com.p2p.presentation.basegame.GameViewModelFactory

class TuttiFruttiViewModelFactory(
    activity: GameActivity<*, *>,
    data: Data
) : GameViewModelFactory(activity, data) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == TuttiFruttiViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            if (data.gameConnectionType == GameConnectionType.SERVER) {
                super.create(ServerTuttiFruttiViewModel::class.java) as T
            } else {
                super.create(ClientTuttiFruttiViewModel::class.java) as T
            }
        } else {
            throw IllegalStateException("TuttiFruttiViewModelFactory only creates tutti frutti view models")
        }
    }
}