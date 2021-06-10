package com.p2p.presentation.tuttifrutti.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.p2p.presentation.base.BaseViewModel

class ServerLobbyViewModel: BaseViewModel<LobbyEvent>() {
    private val _goToPlayButtonEnabled = MutableLiveData<Boolean>()
    val goToPlayButtonEnabled: LiveData<Boolean> = _goToPlayButtonEnabled
}