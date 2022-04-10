package ar.com.play2play.presentation.impostor

import androidx.lifecycle.ViewModel
import ar.com.play2play.presentation.basegame.GameActivity
import ar.com.play2play.presentation.basegame.GameConnectionType
import ar.com.play2play.presentation.basegame.GameViewModelFactory

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
