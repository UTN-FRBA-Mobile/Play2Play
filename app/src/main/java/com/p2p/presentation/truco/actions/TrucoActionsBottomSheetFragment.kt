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

        trucoButton.setOnClickListener {
            viewModel.performTruco()
        }
        envidoButton.setOnClickListener { viewModel.performEnvido() }
        realEnvidoButton.setOnClickListener { viewModel.performRealEnvido() }
        //TODO pasarle los puntos del oponente cuando existan los puntos de la ronda
        faltaEnvidoButton.setOnClickListener { viewModel.performFaltaEnvido() }
        goToDeckButton.setOnClickListener { viewModel.goToDeck() }
        changeEnvidoButtonAvailability(false)
        setupObservers()
    }

    override fun setupObservers() {
        super.setupObservers()
        observe(viewModel.envidoButtonEnabled) {
            changeEnvidoButtonAvailability(it)
            envidoDisabledForHand = !it
        }
        observe(viewModel.currentRound) {
            envidoDisabledForHand = it > 1 || envidoDisabledForHand
        }
        observe(viewModel.singleTimeEvent) { onGameEvent(it) }
        observe(viewModel.trucoButtonEnabled) {
            updateTrucoVisibility(it)
        }
        observe(viewModel.goToDeckButtonEnabled) {
            updateGoToDeckVisibility(it)
        }
        observe(viewModel.lastTrucoAction) {
            updateTrucoText(it?.nextAction()?.message(requireContext()))
        }
    }

    private fun onGameEvent(event: GameEvent) = when (event) {
        is TrucoNewHand -> {
            envidoDisabledForHand = false
            changeEnvidoButtonAvailability(true)
        }
        else -> super.onEvent(event)
    }

    private fun updateTrucoVisibility(visible: Boolean) {
        binding.trucoButton.isEnabled = visible
    }

    private fun updateGoToDeckVisibility(visible: Boolean) {
        binding.goToDeckButton.isEnabled = visible
    }

    private fun updateTrucoText(text: String?) {
        binding.trucoButton.text = text ?: resources.getString(R.string.truco_ask_for_truco)
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
        changeEnvidoButtonAvailability(false)
    }

    private fun onExpanded() {
        binding.openButtonIcon.animateRotation(180f)
        changeEnvidoButtonAvailability(!envidoDisabledForHand)
    }

    private fun changeEnvidoButtonAvailability(enabled: Boolean) {
        binding.envidoOptionsButton.isEnabled = enabled
    }

    private fun Dialog.getBottomSheet(): FrameLayout =
        findViewById(com.google.android.material.R.id.design_bottom_sheet)

    companion object {

        private const val SHOW_DELAY = 1_000L

        fun newInstance() = TrucoActionsBottomSheetFragment()
    }
}
