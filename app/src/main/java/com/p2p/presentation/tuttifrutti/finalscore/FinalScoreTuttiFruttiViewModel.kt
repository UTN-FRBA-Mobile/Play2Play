package com.p2p.presentation.tuttifrutti.finalscore

import com.p2p.presentation.base.BaseViewModel

class FinalScoreTuttiFruttiViewModel: BaseViewModel<TuttiFruttiFinalScoreEvent>() {

    fun exit() {
        dispatchSingleTimeEvent(EndTuttiFruttiGame)
    }
}