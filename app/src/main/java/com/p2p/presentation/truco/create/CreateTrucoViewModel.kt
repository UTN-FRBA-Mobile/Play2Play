package com.p2p.presentation.truco.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.extensions.requireValue
import com.p2p.presentation.tuttifrutti.create.categories.GoToSelectRounds

class CreateTrucoViewModel:
    BaseViewModel<CreateTrucoEvents>() {

    fun continueToNextScreen(totalPlayers: Int) {
        dispatchSingleTimeEvent(GoToSelectPoints(totalPlayers))
    }
}