package com.p2p.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.p2p.utils.showSnackBar

/**
 * Base implementation of a [Fragment] used to simplify boilerplate.
 * [VB]: ViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
// TODO: reduce boilerplate with flor's PR
abstract class BaseMVVMBottomSheetDialogFragment<VB : ViewBinding, E : Any, VM : BaseViewModel<E>> :
    BaseBottomSheetDialogFragment<VB>() {

    protected abstract val viewModel: VM

    private val observers = mutableListOf<Pair<LiveData<*>, Observer<*>>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe(viewModel.singleTimeEvent) { onEvent(it) }
        observe(viewModel.message) { showSnackBar(it) }
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    protected fun <T> observe(liveData: LiveData<T>, observer: Observer<T>) {
        observers.add(liveData to observer)
        liveData.observe(viewLifecycleOwner, observer)
    }

    /** Invoked when the view is initialized and should be used to setup the observers for the view model. */
    protected open fun setupObservers() {}

    /** Invoked when the view is destroyed and should be used to removed the observers for the view model. */
    @CallSuper
    protected open fun removeObservers() {
        @Suppress("UNCHECKED_CAST")
        observers.forEach { (livedata, observer) -> livedata.removeObserver(observer as Observer<in Any>) }
    }

    /** Invoked when a single time event is dispatched from the view model. */
    protected open fun onEvent(event: E) {}

    /** Show a snackbar customized with the given [data]. */
    protected fun showSnackBar(data: BaseViewModel.MessageData) {
        val view = view ?: return
        requireContext().showSnackBar(view, data)
    }
}
