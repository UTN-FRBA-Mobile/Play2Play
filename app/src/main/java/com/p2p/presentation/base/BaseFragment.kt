package com.p2p.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
abstract class BaseFragment<VB : ViewBinding, E : Any, VM : BaseViewModel<E>> : Fragment() {

    protected abstract val viewModel: VM

    private val observers = mutableListOf<Pair<LiveData<*>, Observer<*>>>()

    /** The binding is used to access to the views declared on the layout [VB]. */
    private var _binding: VB? = null
    protected val binding
        get() = requireNotNull(_binding) {
            "The view binding is required but the view was already destroyed."
        }

    /** It's necessary since ViewBinding doesn't give a simpler way, it should be = T::inflate. */
    abstract val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.singleTimeEvent) { onEvent(it) }
        observe(viewModel.message) { showSnackBar(it) }
        if (savedInstanceState == null) {
            initValues()
        }
        initUI()
        setupObservers()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
        _binding = null
    }

    /** Invoked when the view is initialized and should be used to setup the observers for the view model. */
    protected open fun setupObservers() {}

    /** Invoked when the view is destroyed and should be used to removed the observers for the view model. */
    @CallSuper
    protected open fun removeObservers() {
        @Suppress("UNCHECKED_CAST")
        observers.forEach { (livedata, observer) -> livedata.removeObserver(observer as Observer<in Any>) }
    }

    /** Invoked when the view is initialized and should initialize the view that requires it. */
    protected open fun initUI() {}

    /** Invoked when the view is initialized and should initialize the view that requires it. */
    protected open fun initValues() {}

    /** Invoked when a single time event is dispatched from the view model. */
    protected open fun onEvent(event: E) {}

    protected fun <T> observe(liveData: LiveData<T>, observer: Observer<T>) {
        observers.add(liveData to observer)
        liveData.observe(viewLifecycleOwner, observer)
    }

    /** Show a snackbar customized with the given [data]. */
    protected fun showSnackBar(data: BaseViewModel.MessageData) {
        val view = view ?: return
        requireContext().showSnackBar(view, data)
    }
}
