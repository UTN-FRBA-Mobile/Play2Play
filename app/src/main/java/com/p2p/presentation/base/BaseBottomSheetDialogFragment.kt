package com.p2p.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p2p.R
import com.p2p.utils.showSnackBar

/**
 * Base implementation of a [Fragment] used to simplify boilerplate.
 * [VB]: ViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
// TODO: reduce boilerplate with flor's PR
abstract class BaseBottomSheetDialogFragment<VB : ViewBinding, E : Any, VM : BaseViewModel<E>> :
    BottomSheetDialogFragment() {

    protected abstract val viewModel: VM
    protected open val isCollapsable = true

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            if (!isCollapsable) {
                setOnShowListener {
                    val dialog = it as BottomSheetDialog
                    val bottomSheet = dialog
                        .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                        ?: return@setOnShowListener
                    BottomSheetBehavior.from(bottomSheet).apply {
                        state = BottomSheetBehavior.STATE_EXPANDED
                        addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                            override fun onStateChanged(bottomSheet: View, newState: Int) {
                                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                                    state = BottomSheetBehavior.STATE_EXPANDED
                                }
                            }

                            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            }
                        })
                    }
                }
            }
        }
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
        requireContext().showSnackBar(view, data)
    }
}
