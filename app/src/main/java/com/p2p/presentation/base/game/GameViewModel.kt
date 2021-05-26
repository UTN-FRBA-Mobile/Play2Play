package com.p2p.presentation.base.game

import com.p2p.presentation.base.BaseViewModel

abstract class GameViewModel<E: Any> : BaseViewModel<E>()  {
    abstract fun onStart()

    abstract fun onCreateOrJoin()
}