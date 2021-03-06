package ar.com.play2play.presentation.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/** Base implementation of a [ViewModel] used to simplify boilerplate. */
abstract class BaseViewModel<E : Any> : ViewModel() {

    /** Represents an event that should update the UI only once. E.g.: opening a new screen. */
    private val _singleTimeEvent = SingleLiveEvent<E>()
    val singleTimeEvent: LiveData<E> = _singleTimeEvent

    /** Represents an event that will show a SnackBar message. */
    private val _message = SingleLiveEvent<MessageData>()
    val message: LiveData<MessageData> = _message

    /** Dispatch a new event that will update the UI only once. */
    protected fun dispatchSingleTimeEvent(event: E) {
        _singleTimeEvent.value = event
    }

    /** Dispatch a new event that will show a message. */
    protected fun dispatchMessage(
        @StringRes textRes: Int? = null,
        text: String? = null,
        type: MessageData.Type,
        duration: MessageData.Duration = MessageData.Duration.SHORT,
        vararg formatArgs: Any = emptyArray()
    ) {
        _message.value = MessageData(textRes, text, type, duration, *formatArgs)
    }

    class MessageData(
        @StringRes val textRes: Int? = null,
        val text: String? = null,
        val type: Type,
        val duration: Duration = Duration.SHORT,
        vararg val formatArgs: Any = emptyArray(),
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
