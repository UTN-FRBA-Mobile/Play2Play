package com.p2p.presentation.truco

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.p2p.R
import com.p2p.databinding.ViewTrucoEarnedPointsBinding
import com.p2p.databinding.ViewTrucoHeaderBinding
import com.p2p.model.truco.Card
import com.p2p.presentation.base.NoViewModel
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.basegame.PauseGame
import com.p2p.presentation.extensions.animateBackgroundTint
import com.p2p.presentation.extensions.fadeIn
import com.p2p.presentation.extensions.fadeOut
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoActionAvailableResponses
import com.p2p.presentation.truco.actions.TrucoActionsBottomSheetFragment
import com.p2p.presentation.truco.cards.CardImageCreator
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.utils.fromHtml
import com.p2p.utils.setOnEndListener

abstract class TrucoFragment<VB : ViewBinding> :
    BaseGameFragment<VB, Any, NoViewModel, TrucoViewModel>(),
    TrucoCardsHand.Listener {

    override val viewModel by viewModels<NoViewModel>()
    override val isHeaderVisible: Boolean = false

    private lateinit var headerBinding: ViewTrucoHeaderBinding
    private lateinit var earnedPointsBinding: ViewTrucoEarnedPointsBinding
    protected lateinit var myCardsHand: TrucoCardsHand

    private lateinit var roundViews: List<View>

    private lateinit var myCardsViews: List<ImageView>
    protected lateinit var myDroppingPlacesViews: List<View>

    private val bottomSheet by lazy { TrucoActionsBottomSheetFragment.newInstance() }

    private var isFirstHand = true

    private val shortDuration by lazy {
        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }
    private val longDuration by lazy {
        resources.getInteger(android.R.integer.config_longAnimTime).toLong()
    }

    protected val myActionBubble: Pair<View, TextView> by lazy {
        val (view, textView) = bubbleForPosition(TrucoPlayerPosition.MY_SELF)
        requireView().findViewById<View>(view) to requireView().findViewById(textView)
    }

    override fun setupObservers() {
        super.setupObservers()
        observe(gameViewModel.myCards) { initMyCardsHand(it) }
        observe(gameViewModel.singleTimeEvent) { onGameEvent(it) }
        observe(gameViewModel.actionAvailableResponses) { updateActionAvailableResponses(it) }
        observe(gameViewModel.ourScore) { updateScore(headerBinding.ourScore, it) }
        observe(gameViewModel.theirScore) { updateScore(headerBinding.theirScore, it) }
        observe(gameViewModel.currentTurnPlayerPosition) { updateCurrentTurn(it) }
        observe(gameViewModel.trucoAccumulatedPoints) {
            requireView().findViewById<TextView>(R.id.truco_points).text = resources
                .getQuantityString(R.plurals.truco_accumulated_points, it, it)
                .fromHtml()
        }
    }

    override fun initUI() = with(requireView()) {
        super.initUI()
        headerBinding = ViewTrucoHeaderBinding.bind(gameBinding.root)
        earnedPointsBinding = ViewTrucoEarnedPointsBinding.bind(gameBinding.root)
        roundViews =
            listOf(headerBinding.firstRound, headerBinding.secondRound, headerBinding.thirdRound)
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
            gameViewModel.performEnvido(isReply = true)
        }
        findViewById<View>(R.id.action_response_yes_real_envido).setOnClickListener {
            gameViewModel.performRealEnvido(isReply = true)
        }
        findViewById<View>(R.id.action_response_yes_falta_envido).setOnClickListener {
            gameViewModel.performFaltaEnvido(isReply = true)
        }
        findViewById<View>(R.id.action_response_yes_retruco).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.Retruco)
        }
        findViewById<View>(R.id.action_response_yes_vale_cuatro).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.ValeCuatro)
        }
        findViewById<View>(R.id.action_response_envido_goes_first).setOnClickListener {
            gameViewModel.replyAction(TrucoAction.EnvidoGoesFirst)
        }
    }

    final override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {
        gameViewModel.playCard(playingCard.card)
    }

    abstract fun initializeRivalHands(isFirstHand: Boolean)

    abstract fun createMyCardsHand(myPlayingCards: List<TrucoCardsHand.PlayingCard>): TrucoCardsHand

    abstract fun getPlayerCardsHand(playerPosition: TrucoPlayerPosition): TrucoCardsHand

    abstract fun getPlayerBubbleWithTextView(playerPosition: TrucoPlayerPosition): Pair<View, TextView>

    abstract fun getDroppingPlaces(playerPosition: TrucoPlayerPosition): List<View>

    abstract fun hideAllActions()

    @CallSuper
    protected open fun onGameEvent(event: GameEvent) = when (event) {
        is TrucoShowActionEvent ->
            showPlayerAction(event.playerPosition, event.action, event.canAnswer, event.onComplete)
        is TrucoShowManyActionsEvent -> showManyActions(event.actionByPlayer, event.onComplete)
        is TrucoFinishRound -> finishRound(event.round, event.result)
        is TrucoNewHand -> newHand()
        is TrucoOtherPlayedCardEvent -> onOtherPlayedCard(event)
        is TrucoShowEarnedPoints -> showEarnedPoints(
            event.isMyTeam,
            event.earnedPoints,
            event.onComplete
        )
        TrucoTakeTurnEvent -> takeTurn()
        else -> {
            if (event is PauseGame) deleteTrucoActionsBottomSheet()
            super.onEvent(event)
        }
    }

    private fun newHand() {
        clearRoundWinners()
        myDroppingPlacesViews.forEach { it.isInvisible = true }
        initializeRivalHands(isFirstHand = isFirstHand)
        isFirstHand = false
    }

    protected fun hideActionBubble(bubbleView: View, bubbleTextView: TextView) {
        hideBubbleView(bubbleView)
        hideBubbleView(bubbleTextView)
    }

    abstract fun bubbleForPosition(playerPosition: TrucoPlayerPosition): Pair<Int, Int>

    protected fun getPlayingCards(cardsViews: List<ImageView>, cards: List<Card>) =
        cardsViews.mapIndexed { i, view ->
            TrucoCardsHand.PlayingCard(cards[i], view)
        }

    protected fun loadCardImages(cardViews: List<ImageView>, cards: List<Card?>) =
        cardViews.forEachIndexed { i, view ->
            CardImageCreator.loadCard(view, cards.getOrNull(i))
        }

    @CallSuper
    protected open fun updateCurrentTurn(playerPosition: TrucoPlayerPosition) {
        if (playerPosition == TrucoPlayerPosition.MY_SELF) {
            addActionsBottomSheet()
        } else {
            deleteTrucoActionsBottomSheet()
        }
    }

    private fun takeTurn() {
        myCardsHand.takeTurn()
        view?.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    private fun showPlayerAction(
        position: TrucoPlayerPosition,
        action: TrucoAction,
        canAnswer: Boolean,
        onComplete: () -> Unit
    ) {
        val (bubble, text) = getPlayerBubbleWithTextView(position)
        showAction(bubble, text, action, onComplete)
        if (canAnswer) {
            updateActionAvailableResponses(action.availableResponses())
        }
    }

    private fun showManyActions(
        actionsByPlayer: List<Pair<TrucoPlayerPosition, TrucoAction>>,
        onComplete: () -> Unit
    ) = showOneOfManyActions(actionsByPlayer, onComplete)

    private fun showOneOfManyActions(
        actions: List<Pair<TrucoPlayerPosition, TrucoAction>>,
        finalOnComplete: () -> Unit
    ) {
        val action = actions.first()
        showPlayerAction(
            action.first,
            action.second,
            canAnswer = false,
            onComplete = {
                val pendingActions = actions.drop(1)
                if (pendingActions.isEmpty()) {
                    finalOnComplete()
                } else {
                    showOneOfManyActions(pendingActions, finalOnComplete)
                }
            }
        )
    }

    private fun showAction(
        bubbleBackground: View,
        bubbleText: TextView,
        action: TrucoAction,
        onComplete: () -> Unit
    ) {
        if (bubbleBackground.scaleX >= MIN_ACTION_BUBBLE_VISIBLE_SCALING) {
            hideBubbleView(bubbleBackground)
            hideBubbleView(bubbleText) {
                showActionAfterVisibilityCheck(bubbleBackground, bubbleText, action, onComplete)
                bubbleText.animate().setListener(null)
            }
        } else {
            showActionAfterVisibilityCheck(bubbleBackground, bubbleText, action, onComplete)
        }
    }

    private fun addActionsBottomSheet() {
        if (activity == null || bottomSheet.isAdded) return
        bottomSheet.show(parentFragmentManager, ACTIONS_BOTTOM_SHEET_TAG)
    }

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

    private fun showActionAfterVisibilityCheck(
        bubbleBackground: View,
        bubbleText: TextView,
        action: TrucoAction,
        onComplete: () -> Unit
    ) {
        hideTrucoActionsBottomSheet()
        hideActions()
        bubbleText.text = action.message(requireContext())
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
            bubbleBackground.postDelayed(
                {
                    hideActions()
                    onComplete()
                },
                HIDE_ACTION_BUBBLES_DELAY
            )
        }
    }

    private fun deleteTrucoActionsBottomSheet() {
        (parentFragmentManager.findFragmentByTag(ACTIONS_BOTTOM_SHEET_TAG) as TrucoActionsBottomSheetFragment?)?.run {
            isVisible(false) { dismiss() }
        }
    }

    private fun hideTrucoActionsBottomSheet() {
        (parentFragmentManager
            .findFragmentByTag(ACTIONS_BOTTOM_SHEET_TAG) as TrucoActionsBottomSheetFragment?)
            ?.isVisible(false)
    }

    private fun showTrucoActionsBottomSheet() {
        (parentFragmentManager
            .findFragmentByTag(ACTIONS_BOTTOM_SHEET_TAG) as TrucoActionsBottomSheetFragment?)
            ?.isVisible(true)
    }

    private fun hideActions() {
        showTrucoActionsBottomSheet()
        hideActionBubble(
            requireView().findViewById(R.id.my_action_bubble),
            requireView().findViewById(R.id.my_action_bubble_text)
        )
        hideAllActions()
        requireView().findViewById<View>(R.id.action_background).animate()
            .alpha(0f)
            .setOnEndListener {
                requireView().findViewById<View>(R.id.action_background).isVisible = false
            }
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
            findViewById<View>(R.id.action_response_envido_goes_first).isVisible = envidoGoesFirst
            val actionResponseContainer = findViewById<View>(R.id.actions_responses)
            if (hasAvailableResponses()) actionResponseContainer.fadeIn() else actionResponseContainer.fadeOut()
        }
    }

    private fun initMyCardsHand(myCards: List<Card>) {
        val myPlayingCards = getPlayingCards(myCardsViews, myCards)
        myCardsHand = createMyCardsHand(myPlayingCards)
        loadCardImages(myCardsViews, myCards)
        gameViewModel.onMyCardsLoad()
    }

    private fun onOtherPlayedCard(event: TrucoOtherPlayedCardEvent) {
        val roundAsIndex = event.round - 1
        val droppingPlace = getDroppingPlaces(event.playerPosition)[roundAsIndex]
        getPlayerCardsHand(event.playerPosition).playCard(event.card, droppingPlace, roundAsIndex)
    }

    private fun finishRound(round: Int, result: TrucoRoundResult) {
        roundViews[round - 1].animateBackgroundTint(
            ContextCompat.getColor(requireContext(), result.color)
        ) {
            val colorPrimary = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
            roundViews.getOrNull(round)?.backgroundTintList = colorPrimary
        }
    }


    private fun clearRoundWinners() {
        roundViews[0].backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        )
        roundViews[1].backgroundTintList = null
        roundViews[2].backgroundTintList = null
    }

    private fun showEarnedPoints(
        isMyTeam: Boolean,
        earnedPoints: Int,
        onComplete: () -> Unit
    ) = with(earnedPointsBinding) {
        title.setText(if (isMyTeam) R.string.truco_our_score_label else R.string.truco_their_score_label)
        @SuppressLint("SetTextI18n")
        subtitle.text = "+$earnedPoints"
        container.fadeIn()
        container.postDelayed(
            {
                container.fadeOut()
                onComplete()
            },
            EARNED_POINTS_DELAY_MS
        )
        Unit
    }

    companion object {

        private const val SCORE_ZOOM_ANIMATION = 1.2f
        private const val MIN_ACTION_BUBBLE_VISIBLE_SCALING = 0.9f
        const val ACTIONS_BOTTOM_SHEET_TAG = "TRUCO_ACTIONS_BOTTOM_SHEET"
        private const val ACTION_BACKGROUND_FINAL_ALPHA = 0.5f
        private const val HIDE_ACTION_BUBBLES_DELAY = 2_000L
        private const val EARNED_POINTS_DELAY_MS = 3_000L
    }
}
