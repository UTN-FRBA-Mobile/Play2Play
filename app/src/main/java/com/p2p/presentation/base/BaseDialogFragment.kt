package com.p2p.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.p2p.R

abstract class BaseDialogFragment<VB : ViewBinding, E : Any, VM : BaseViewModel<E>> : DialogFragment() {

    protected abstract val viewModel: VM

    /** The binding is used to access to the views declared on the layout [VB]. */
    private var _binding: VB? = null
    protected val binding
        get() = requireNotNull(_binding) {
            "The view binding is required but the view was already destroyed."
        }

    /** It's necessary since ViewBinding doesn't give a simpler way, it should be = T::inflate. */
    abstract val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    private val observers = mutableListOf<Pair<LiveData<*>, Observer<*>>>()

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
        initUI()
        setupObservers()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
        _binding = null
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


    /** Invoked when the view is initialized and should initialize the view that requires it. */
    protected open fun initUI() {}

    /** Invoked when a single time event is dispatched from the view model. */
    protected open fun onEvent(event: E) {}

    /** Show a snackbar customized with the given [data]. */
    protected fun showSnackBar(data: BaseViewModel.MessageData) {
        val view = view ?: return
        val text = data.textRes?.let { context?.getText(it) } ?: data.text ?: return
        val duration = when (data.duration) {
            BaseViewModel.MessageData.Duration.SHORT -> Snackbar.LENGTH_SHORT
            BaseViewModel.MessageData.Duration.LONG -> Snackbar.LENGTH_LONG
        }
        val backgroundColor = when (data.type) {
            BaseViewModel.MessageData.Type.ERROR -> R.color.design_default_color_error
        }
        Snackbar
            .make(view, text, duration)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), backgroundColor))
            .show()
    }
}