package com.p2p.presentation.base

abstract class BaseMVVMActivity<E : Any, VM : BaseViewModel<E>> : BaseActivity() {

    protected abstract val viewModel: VM

    override fun onStart() {
        super.onStart()
        viewModel.singleTimeEvent.observe(this) { onEvent(it) }
    }

    /** Invoked when a single time event is dispatched from the view model. */
    protected open fun onEvent(event: E) {}
}