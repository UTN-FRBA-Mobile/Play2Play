package ar.com.play2play.presentation.tuttifrutti

import androidx.lifecycle.ViewModel
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.framework.InstructionsLocalResourcesSource
import ar.com.play2play.framework.LoadingTextLocalResourcesSource
import ar.com.play2play.presentation.basegame.GameActivity
import ar.com.play2play.presentation.basegame.GameConnectionType
import ar.com.play2play.presentation.basegame.GameViewModelFactory

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
