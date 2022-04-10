package ar.com.play2play.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import ar.com.play2play.R
import ar.com.play2play.presentation.basegame.GameEvent

abstract class BaseMVVMActivity<E : GameEvent, VM : BaseViewModel<out E>>(
    @LayoutRes layout: Int = R.layout.activity_base
) : BaseActivity(layout) {

    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.singleTimeEvent.observe(this) { onEvent(it) }
    }

    /** Invoked when a single time event is dispatched from the view model. */
    protected open fun onEvent(event: E) {}
}