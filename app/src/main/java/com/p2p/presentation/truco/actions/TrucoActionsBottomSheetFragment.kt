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
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.p2p.R
import com.p2p.databinding.ViewTrucoActionsBinding
import com.p2p.presentation.base.BaseMVVMBottomSheetDialogFragment
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.extensions.animateRotation
import com.p2p.presentation.extensions.fadeIn
import com.p2p.presentation.truco.TrucoNewHand
import com.p2p.presentation.truco.TrucoViewModel

class TrucoActionsBottomSheetFragment :
    BaseMVVMBottomSheetDialogFragment<ViewTrucoActionsBinding, GameEvent, TrucoViewModel>() {

    override val viewModel: TrucoViewModel by activityViewModels()

    private var isExpanded = false

    /** Once envido is asked in a hand, it can't be asked again. */
    private var envidoDisabledForHand = false

    /** Once truco is asked in a hand, neither it or envido can be asked. */
    private var trucoDisabledForHand = false

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

    override fun initUI() = with(binding) {
        actionsBottomSheet.postDelayed({ actionsBottomSheet.fadeIn() }, SHOW_DELAY)
        openButton.setOnClickListener { toggleState() }
        envidoOptionsButton.setOnClickListener { toggleEnvidoOptionsState() }
        trucoButton.setOnClickListener { viewModel.performAction(TrucoAction.Trucazo) }
        envidoButton.setOnClickListener { viewModel.performAction(TrucoAction.Envido(false)) }
        realEnvidoButton.setOnClickListener { viewModel.performAction(TrucoAction.RealEnvido) }
        //TODO pasarle los puntos del oponente cuando existan los puntos de la ronda
        faltaEnvidoButton.setOnClickListener { viewModel.performAction(TrucoAction.FaltaEnvido(0)) }
        goToDeckButton.setOnClickListener { viewModel.performAction(TrucoAction.GoToDeck) }
        disableEnvidoAndTrucoButtons()
        setupObservers()
    }

    override fun setupObservers() {
        super.setupObservers()
        observe(viewModel.trucoAlreadyAsked) {
            if (it) {
                envidoDisabledForHand = true
                trucoDisabledForHand = true
                disableEnvidoAndTrucoButtons()
            }
        }
        observe(viewModel.envidoAlreadyAsked) { if (it) disableEnvidoForHand() }
        observe(viewModel.currentRound) { if (it > 1) disableEnvidoForHand() } // Envido can only be asked on round 1
        observe(viewModel.singleTimeEvent) { onGameEvent(it) }
    }

    private fun onGameEvent(event: GameEvent) = when (event) {
        is TrucoNewHand -> {
            trucoDisabledForHand = false
            envidoDisabledForHand = false
            enableEnvidoAndTrucoButtons()
        }
        else -> super.onEvent(event)
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
        disableEnvidoAndTrucoButtons()
    }

    private fun onExpanded() {
        binding.openButtonIcon.animateRotation(180f)
        enableEnvidoAndTrucoButtons()
    }

    private fun disableEnvidoAndTrucoButtons() {
        changeEnvidoButtonAvailability(false)
        changeTrucoButtonAvailability(false)
    }

    private fun enableEnvidoAndTrucoButtons() {
        changeEnvidoButtonAvailability(true)
        changeTrucoButtonAvailability(true)
    }

    private fun disableEnvidoForHand() {
        changeEnvidoButtonAvailability(false)
        envidoDisabledForHand = true
    }

    private fun changeEnvidoButtonAvailability(enabled: Boolean) {
        binding.envidoOptionsButton.isEnabled = enabled && !envidoDisabledForHand
    }

    private fun changeTrucoButtonAvailability(enabled: Boolean) {
        binding.trucoButton.isEnabled = (enabled && !trucoDisabledForHand)
        binding.trucoButton.alpha = if (enabled && !trucoDisabledForHand) 1f else 0.3f
    }

    private fun Dialog.getBottomSheet(): FrameLayout {
        return findViewById(com.google.android.material.R.id.design_bottom_sheet)
    }

    companion object {

        private const val SHOW_DELAY = 1_000L

        fun newInstance() = TrucoActionsBottomSheetFragment()
    }
}
