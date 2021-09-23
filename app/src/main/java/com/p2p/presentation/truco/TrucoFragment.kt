package com.p2p.presentation.truco

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p2p.R
import com.p2p.databinding.ViewTrucoHeaderBinding
import com.p2p.model.truco.Card
import com.p2p.presentation.base.NoViewModel
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.extensions.animateBackgroundTint
import com.p2p.presentation.extensions.fadeIn
import com.p2p.presentation.extensions.fadeOut
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses
import com.p2p.presentation.truco.actions.TrucoActionsBottomSheetFragment
import com.p2p.presentation.truco.cards.CardImageCreator
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.utils.setOnEndListener

abstract class TrucoFragment<VB : ViewBinding> :
    BaseGameFragment<VB, Any, NoViewModel, TrucoViewModel>(),
    TrucoCardsHand.Listener {

    override val viewModel by viewModels<NoViewModel>()
    override val isHeaderVisible: Boolean = false

    protected lateinit var headerBinding: ViewTrucoHeaderBinding
    protected lateinit var myCardsHand: TrucoCardsHand

    protected val cardsImageCreator by lazy { CardImageCreator(requireContext()) }
    protected lateinit var roundViews: List<View>

    protected lateinit var myCardsViews: List<ImageView>
    protected lateinit var myDroppingPlacesViews: List<View>

    private val shortDuration by lazy { resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }
    private val longDuration by lazy { resources.getInteger(android.R.integer.config_longAnimTime).toLong() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            addActionsBottomSheet()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        observe(gameViewModel.singleTimeEvent) { onGameEvent(it) }
        observe(gameViewModel.actionAvailableResponses) { updateActionAvailableResponses(it) }
    }

    override fun initUI() = with(requireView()) {
        super.initUI()
        headerBinding = ViewTrucoHeaderBinding.bind(gameBinding.root)
        roundViews = listOf(headerBinding.firstRound, headerBinding.secondRound, headerBinding.thirdRound)
        myCardsViews = listOf(
            findViewById(R.id.my_left_card),
            findViewById(R.id.my_middle_card),
            findViewById(R.id.my_right_card)
        )
        myDroppingPlacesViews = listOf(
            findViewById(R.id.drop_first_card),
            findViewById(R.id.drop_second_card),
            findViewById(R.id.drop_third_card)
        )
        findViewById<View>(R.id.action_response_yes_i_do).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.YesIDo)
        }
        findViewById<View>(R.id.action_response_no_i_dont).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.NoIDont)
        }
        findViewById<View>(R.id.action_response_yes_envido).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.Envido(true))
        }
        findViewById<View>(R.id.action_response_yes_real_envido).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.RealEnvido)
        }
        findViewById<View>(R.id.action_response_yes_falta_envido).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.FaltaEnvido)
        }
        findViewById<View>(R.id.action_response_yes_retruco).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.Retruco)
        }
        findViewById<View>(R.id.action_response_yes_vale_cuatro).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.ValeCuatro)
        }
    }

    abstract fun mockReplyToMyAction(action: TrucoAction) // TODO: delete

    abstract fun mockReplyToTheirAction(action: TrucoAction) // TODO: delete

    abstract fun hideAllActions()

    @CallSuper
    protected open fun onGameEvent(event: GameEvent) = when (event) {
        is TrucoShowMyActionEvent -> showMyAction(event.action)
        else -> super.onEvent(event)
    }

    protected fun updateScores(ourScore: Int, their: Int) {
        updateScore(headerBinding.ourScore, ourScore)
        updateScore(headerBinding.theirScore, their)
    }

    protected fun hideActionBubble(bubbleView: View, bubbleTextView: TextView) {
        hideBubbleView(bubbleView)
        hideBubbleView(bubbleTextView)
    }

    protected fun showRivalAction(rivalActionBubble: View, rivalActionTextView: TextView, action: TrucoAction) {
        showAction(rivalActionBubble, rivalActionTextView, action)
        updateActionAvailableResponses(action.getAvailableResponses())
        mockReplyToTheirAction(action) // TODO: delete
    }

    protected fun getPlayingCards(cardsViews: List<ImageView>, cards: List<Card>) = cardsViews.mapIndexed { i, view ->
        TrucoCardsHand.PlayingCard(cards[i], view)
    }

    protected fun takeTurn() = myCardsHand.takeTurn()

    protected fun finishRound(round: Int, result: TrucoRoundResult) {
        roundViews[round].animateBackgroundTint(ContextCompat.getColor(requireContext(), result.color)) {
            val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            roundViews.getOrNull(round + 1)?.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        }
    }

    protected fun loadCardImages(cardViews: List<ImageView>, cards: List<Card?>) = cardViews.forEachIndexed { i, view ->
        val (image, description) = cardsImageCreator.create(cards.getOrNull(i))
        view.setImageBitmap(image)
        view.contentDescription = description
    }

    protected fun showMyAction(action: TrucoAction) {
        val bubbleBackground = requireView().findViewById<View>(R.id.my_action_bubble)
        val bubbleText = requireView().findViewById<TextView>(R.id.my_action_bubble_text)
        showAction(bubbleBackground, bubbleText, action)
        mockReplyToMyAction(action) // TODO: delete
    }

    private fun showAction(bubbleBackground: View, bubbleText: TextView, action: TrucoAction) {
        if (bubbleBackground.scaleX >= MIN_ACTION_BUBBLE_VISIBLE_SCALING) {
            hideBubbleView(bubbleBackground)
            hideBubbleView(bubbleText) {
                showActionAfterVisibilityCheck(bubbleBackground, bubbleText, action)
                bubbleText.animate().setListener(null)
            }
        } else {
            showActionAfterVisibilityCheck(bubbleBackground, bubbleText, action)
        }
    }

    private fun addActionsBottomSheet() = TrucoActionsBottomSheetFragment
        .newInstance()
        .show(parentFragmentManager, ACTIONS_BOTTOM_SHEET_TAG)

    private fun updateScore(textView: TextView, score: Int) = when (score) {
        0 -> textView.text = score.toString()
        textView.text?.toString()?.toIntOrNull() -> Unit
        else -> textView
            .animate()
            .scaleX(SCORE_ZOOM_ANIMATION)
            .scaleY(SCORE_ZOOM_ANIMATION)
            .setOnEndListener {
                textView.text = score.toString()
                textView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .start()
            }
            .start()
    }

    private fun hideBubbleView(view: View, onEndListener: () -> Unit = { }) = view.animate()
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(shortDuration)
        .setInterpolator(null)
        .setOnEndListener(onEndListener)
        .start()

    private fun showActionAfterVisibilityCheck(bubbleBackground: View, bubbleText: TextView, action: TrucoAction) {
        (parentFragmentManager.findFragmentByTag(ACTIONS_BOTTOM_SHEET_TAG) as BottomSheetDialogFragment?)?.dismiss()
        bubbleText.text = action.getMessage(requireContext())
        showBubbleView(bubbleBackground)
        showBubbleView(bubbleText)
        val actionBackground = requireView().findViewById<View>(R.id.action_background)
        actionBackground.isVisible = true
        actionBackground
            .animate()
            .setListener(null)
            .alpha(ACTION_BACKGROUND_FINAL_ALPHA)
            .start()
        if (!action.hasReplication) {
            bubbleBackground.postDelayed({ hideActions() }, HIDE_ACTION_BUBBLES_DELAY)
        }
    }

    private fun hideActions() {
        addActionsBottomSheet()
        hideActionBubble(
            requireView().findViewById(R.id.my_action_bubble),
            requireView().findViewById(R.id.my_action_bubble_text)
        )
        hideAllActions()
        requireView().findViewById<View>(R.id.action_background).animate()
            .alpha(0f)
            .setOnEndListener { requireView().findViewById<View>(R.id.action_background).isVisible = false }
            .start()
        requireView().findViewById<View>(R.id.actions_responses).fadeOut()
    }

    private fun showBubbleView(view: View) = view.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(longDuration)
        .setInterpolator(BounceInterpolator())
        .start()

    private fun updateActionAvailableResponses(
        availableResponses: TrucoActionAvailableResponses
    ) = with(availableResponses) {
        with(requireView()) {
            findViewById<View>(R.id.action_response_yes_i_do).isVisible = iDo
            findViewById<View>(R.id.action_response_no_i_dont).isVisible = iDont
            findViewById<View>(R.id.action_response_yes_envido).isVisible = envido
            findViewById<View>(R.id.action_response_yes_real_envido).isVisible = realEnvido
            findViewById<View>(R.id.action_response_yes_falta_envido).isVisible = faltaEnvido
            findViewById<View>(R.id.action_response_yes_retruco).isVisible = retruco
            findViewById<View>(R.id.action_response_yes_vale_cuatro).isVisible = valeCuatro
            val actionResponseContainer = findViewById<View>(R.id.actions_responses)
            if (hasAvailableResponses()) actionResponseContainer.fadeIn() else actionResponseContainer.fadeOut()
        }
    }

    companion object {

        private const val SCORE_ZOOM_ANIMATION = 1.2f
        private const val MIN_ACTION_BUBBLE_VISIBLE_SCALING = 0.9f
        private const val ACTIONS_BOTTOM_SHEET_TAG = "TRUCO_ACTIONS_BOTTOM_SHEET"
        private const val ACTION_BACKGROUND_FINAL_ALPHA = 0.5f
        private const val HIDE_ACTION_BUBBLES_DELAY = 3_000L
    }
}