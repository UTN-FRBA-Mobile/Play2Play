package ar.com.play2play.presentation.base

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/** A single live event is a live data that will be consumed just once. */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val isPending = AtomicBoolean(false)
    private val observers = mutableSetOf<Observer<in T>>()

    private val internalObserver = Observer<T> { t ->
        if (isPending.compareAndSet(true, false)) {
            observers.forEach { observer -> observer.onChanged(t) }
        }
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observers.add(observer)

        if (!hasObservers()) {
            super.observe(owner, internalObserver)
        }
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observers.clear()
        super.removeObservers(owner)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observers.remove(observer)
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        isPending.set(true)
        super.setValue(t)
    }
}
