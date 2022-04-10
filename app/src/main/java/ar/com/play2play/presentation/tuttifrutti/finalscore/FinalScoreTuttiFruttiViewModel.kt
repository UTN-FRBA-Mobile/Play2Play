package ar.com.play2play.presentation.tuttifrutti.finalscore

import ar.com.play2play.presentation.base.BaseViewModel

class FinalScoreTuttiFruttiViewModel: BaseViewModel<TuttiFruttiFinalScoreEvent>() {

    fun exit() {
        dispatchSingleTimeEvent(EndTuttiFruttiGame)
    }
}