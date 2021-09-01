package com.p2p.presentation.truco.actions

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.p2p.R
import com.p2p.TrucoActivity
import com.p2p.databinding.ViewTrucoActionsBinding
import com.p2p.presentation.base.BaseBottomSheetDialogFragment
import com.p2p.presentation.extensions.animateRotation

class TrucoActionsBottomSheetFragment : BaseBottomSheetDialogFragment<ViewTrucoActionsBinding>() {

    private var isExpanded = false
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isExpanded = when (newState) {
                STATE_COLLAPSED -> {
                    onCollapsed()
                    false
                }
                STATE_EXPANDED -> {
                    onExpanded()
                    true
                }
                else -> return
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewTrucoActionsBinding =
        ViewTrucoActionsBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
        isCancelable = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0f)
            setOnShowListener {
                val dialog = it as BottomSheetDialog
                val bottomSheet = dialog.getBottomSheet()
                val maxHeight = requireActivity().resources.displayMetrics.heightPixels * 0.07
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.peekHeight = maxHeight.toInt()
                bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
                dialog.findViewById<View>(R.id.touch_outside)?.apply {
                    setOnTouchListener { view, event ->
                        event.setLocation(event.rawX - view.x, event.rawY - view.y)
                        activity?.dispatchTouchEvent(event)
                        false
                    }
                }

            }
        }
    }

    override fun initUI() {
        binding.openButton.setOnClickListener { toggleState() }
        binding.envidoOptionsButton.setOnClickListener { toggleEnvidoOptionsState() }
        binding.trucoButton.setOnClickListener { (activity as TrucoActivity).showMyAction(TrucoAction.Truco) }
        binding.envidoButton.setOnClickListener { (activity as TrucoActivity).showMyAction(TrucoAction.Envido(false)) }
        binding.realEnvidoButton.setOnClickListener { (activity as TrucoActivity).showMyAction(TrucoAction.RealEnvido) }
        binding.faltaEnvidoButton.setOnClickListener { (activity as TrucoActivity).showMyAction(TrucoAction.FaltaEnvido) }
    }

    private fun toggleState() {
        dialog
            ?.getBottomSheet()
            ?.let { BottomSheetBehavior.from(it) }
            ?.state = if (isExpanded) STATE_COLLAPSED else STATE_EXPANDED
    }

    private fun toggleEnvidoOptionsState() {
        TransitionManager.beginDelayedTransition(binding.envidoContainer)
        binding.envidoContainer.isVisible = !binding.envidoContainer.isVisible
        binding.envidoButtonArrow.animateRotation(if (binding.envidoContainer.isVisible) 0f else 180f)
    }

    private fun onCollapsed() {
        binding.openButtonIcon.animateRotation(0f)
    }

    private fun onExpanded() {
        binding.openButtonIcon.animateRotation(180f)
    }

    private fun Dialog.getBottomSheet(): FrameLayout {
        return findViewById(com.google.android.material.R.id.design_bottom_sheet)
    }

    companion object {

        fun newInstance() = TrucoActionsBottomSheetFragment()
    }
}
