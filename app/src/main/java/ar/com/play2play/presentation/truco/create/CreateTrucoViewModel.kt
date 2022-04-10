package ar.com.play2play.presentation.truco.create

import ar.com.play2play.presentation.base.BaseViewModel

class CreateTrucoViewModel:
    BaseViewModel<CreateTrucoEvents>() {

    fun continueToNextScreen(totalPlayers: Int) {
        dispatchSingleTimeEvent(GoToSelectPoints(totalPlayers))
    }
}