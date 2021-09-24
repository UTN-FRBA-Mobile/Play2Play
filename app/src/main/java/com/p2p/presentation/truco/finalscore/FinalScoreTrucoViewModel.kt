package com.p2p.presentation.truco.finalscore

import com.p2p.presentation.base.BaseViewModel

class FinalScoreTrucoViewModel: BaseViewModel<TrucoFinalScoreEvent>() {

    fun exit() {
        dispatchSingleTimeEvent(EndTrucoGame)
    }
}