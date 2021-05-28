package com.p2p.presentation.base

import android.os.Bundle
import com.p2p.presentation.basegame.GameEvent

abstract class BaseMVVMActivity<E : GameEvent, VM : BaseViewModel<out E>> : BaseActivity() {

    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.singleTimeEvent.observe(this) { onEvent(it) }
    }

    /** Invoked when a single time event is dispatched from the view model. */
    protected open fun onEvent(event: E) {}
}