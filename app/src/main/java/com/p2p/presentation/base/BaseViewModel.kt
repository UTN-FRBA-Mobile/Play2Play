package com.p2p.presentation.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Base implementation of a [ViewModel] used to simplify boilerplate. */
abstract class BaseViewModel<E : Any> : ViewModel() {

    /** Represents an event that should update the UI only once. E.g.: opening a new screen. */
    private val _singleTimeEvent = SingleLiveEvent<E>()
    val singleTimeEvent: LiveData<E> = _singleTimeEvent

    /** Represents an event that will show a SnackBar message. */
    protected val _message = SingleLiveEvent<MessageData>()
    val message: LiveData<MessageData> = _message

    /** Dispatch a new event that will update the UI only once. */
    protected fun dispatchSingleTimeEvent(event: E) {
        _singleTimeEvent.value = event
    }

    /** Dispatch a new event that will update the UI only once for coroutines on the main dispatcher. */
    protected suspend fun coDispatchSingleTimeEvent(event: E) = withContext(Dispatchers.Main) {
        dispatchSingleTimeEvent(event)
    }

    data class MessageData(
        @StringRes val textRes: Int? = null,
        val text: String? = null,
        val type: Type,
        val duration: Duration = Duration.SHORT
    ) {

        enum class Type {
            ERROR
        }

        enum class Duration {
            SHORT,
            LONG
        }
    }
}
