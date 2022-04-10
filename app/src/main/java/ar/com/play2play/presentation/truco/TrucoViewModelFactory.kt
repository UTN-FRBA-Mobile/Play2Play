package ar.com.play2play.presentation.truco

import androidx.lifecycle.ViewModel
import ar.com.play2play.presentation.basegame.GameActivity
import ar.com.play2play.presentation.basegame.GameConnectionType
import ar.com.play2play.presentation.basegame.GameViewModelFactory

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
