package com.p2p.presentation.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p2p.R

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    /** The binding is used to access to the views declared on the layout [VB]. */
    private var _binding: VB? = null
    protected val binding
        get() = requireNotNull(_binding) {
            "The view binding is required but the view was already destroyed."
        }

    /** It's necessary since ViewBinding doesn't give a simpler way, it should be = T::inflate. */
    abstract val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    protected open val isCollapsable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflater(inflater, container, false)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    /** Invoked when the view is initialized and should initialize the view that requires it. */
    protected open fun initUI() {}
}