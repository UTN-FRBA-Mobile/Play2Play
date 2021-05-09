package com.p2p.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Base implementation of a [ViewModel] used to simplify boilerplate. */
abstract class BaseViewModel<E : Any> : ViewModel() {

    /** Represents an event that should update the UI only once. E.g.: showing a SnackBar. */
    private val _singleTimeEvent = SingleLiveEvent<E>()
    val singleTimeEvent: LiveData<E> = _singleTimeEvent

    /** Dispatch a new event that will update the UI only once. */
    protected fun dispatchSingleTimeEvent(event: E) {
        _singleTimeEvent.value = event
    }

    /** Dispatch a new event that will update the UI only once for coroutines on the main dispatcher. */
    protected suspend fun coDispatchSingleTimeEvent(event: E) = withContext(Dispatchers.Main) {
        dispatchSingleTimeEvent(event)
    }
}
