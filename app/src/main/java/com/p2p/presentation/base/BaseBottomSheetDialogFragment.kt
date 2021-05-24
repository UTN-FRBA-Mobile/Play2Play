package com.p2p.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.p2p.R

/**
 * Base implementation of a [Fragment] used to simplify boilerplate.
 * [VB]: ViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
abstract class BaseBottomSheetDialogFragment<VB : ViewBinding, E : Any, VM : BaseViewModel<E>> : BottomSheetDialogFragment() {

    protected abstract val viewModel: VM

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
        viewModel.singleTimeEvent.observe(viewLifecycleOwner) { onEvent(it) }
        viewModel.message.observe(viewLifecycleOwner) { showSnackBar(it) }
        initUI()
        setupObservers()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Add the [fragment] to the activity and if [shouldAddToBackStack] it'll added to the fragments stack */
    protected fun addFragment(fragment: BaseFragment<*, *, *>, shouldAddToBackStack: Boolean) {
        parentFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
            if (shouldAddToBackStack) addToBackStack(null)
        }
    }

    /** Invoked when the view is initialized and should be used to setup the observers for the view model. */
    protected open fun setupObservers() {}

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
