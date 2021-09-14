package com.p2p.presentation.truco

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p2p.R
import com.p2p.databinding.FragmentPlayTrucoFor2Binding
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
import com.p2p.presentation.truco.cards.TrucoSingleOpponentMyCardsHand
import com.p2p.utils.setOnEndListener

class TrucoPlayFor2Fragment :
    BaseGameFragment<FragmentPlayTrucoFor2Binding, Any, NoViewModel, TrucoViewModel>(),
    TrucoCardsHand.Listener {

    override val viewModel by viewModels<NoViewModel>()
    override val gameViewModel by activityViewModels<TrucoViewModel>()
    override val isHeaderVisible: Boolean = false
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTrucoFor2Binding =
        FragmentPlayTrucoFor2Binding::inflate

    private lateinit var headerBinding: ViewTrucoHeaderBinding
    private lateinit var myCardsHand: TrucoCardsHand

    private val cardsImageCreator by lazy { CardImageCreator(requireContext()) }
    private lateinit var roundViews: List<View>
    private lateinit var myCardsViews: List<ImageView>
    private lateinit var theirCardsViews: List<ImageView>
    private lateinit var myDroppingPlacesViews: List<View>
    private lateinit var theirDroppingPlacesViews: List<View>

    private val shortDuration by lazy { resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }
    private val longDuration by lazy { resources.getInteger(android.R.integer.config_longAnimTime).toLong() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            addActionsBottomSheet()
        }
    }

    override fun initUI() = with(gameBinding) {
        super.initUI()
        headerBinding = ViewTrucoHeaderBinding.bind(root)
        roundViews = listOf(headerBinding.firstRound, headerBinding.secondRound, headerBinding.thirdRound)
        myCardsViews = listOf(myLeftCard, myMiddleCard, myRightCard)
        theirCardsViews = listOf(theirLeftCard, theirMiddleCard, theirRightCard)
        loadCardImages(theirCardsViews, emptyList())
        myDroppingPlacesViews = listOf(dropFirstCard, dropSecondCard, dropThirdCard)
        theirDroppingPlacesViews = listOf(dropTheirFirstCard, dropTheirSecondCard, dropTheirThirdCard)
        updateScores(0, 0)

        with(actionsResponses) {
            actionResponseYesIDo.setOnClickListener { gameViewModel.replyAction(TrucoAction.YesIDo) }
            actionResponseNoIDont.setOnClickListener { gameViewModel.replyAction(TrucoAction.NoIDont) }
            actionResponseYesEnvido.setOnClickListener { gameViewModel.replyAction(TrucoAction.Envido(true)) }
            actionResponseYesRealEnvido.setOnClickListener { gameViewModel.replyAction(TrucoAction.RealEnvido) }
            //TODO pasarle los puntos del oponente cuando existan los puntos de la ronda
            actionResponseYesFaltaEnvido.setOnClickListener { gameViewModel.replyAction(TrucoAction.FaltaEnvido(0)) }
            actionResponseYesRetruco.setOnClickListener { gameViewModel.replyAction(TrucoAction.Retruco) }
            actionResponseYesValeCuatro.setOnClickListener { gameViewModel.replyAction(TrucoAction.ValeCuatro) }
            actionResponseEnvidoGoesFirst.setOnClickListener { gameViewModel.replyAction(TrucoAction.EnvidoGoesFirst) }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        observe(gameViewModel.myCards) { initMyCardsHand(it) }
        observe(gameViewModel.singleTimeEvent) { onGameEvent(it) }
        observe(gameViewModel.actionAvailableResponses) { updateActionAvailableResponses(it) }
    }

    override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {
        //TODO mock on card played
    }

    private fun onGameEvent(event: GameEvent) = when (event) {
        is TrucoShowMyActionEvent -> showMyAction(event.action)
        is TrucoShowOpponentActionEvent -> showOpponentAction(event.action)
        //TODO this is a mock, when played put actual values
        is TrucoFinishRound -> finishRound(1, TrucoRoundResult.WIN)
        is TrucoFinishHand -> TODO("Do finish hand logic")
        is TrucoNewHand -> TODO("Do new hand logic")
        else -> super.onEvent(event)
    }

    private fun addActionsBottomSheet() = TrucoActionsBottomSheetFragment
        .newInstance()
        .show(parentFragmentManager, ACTIONS_BOTTOM_SHEET_TAG)

    private fun loadCardImages(cardViews: List<ImageView>, cards: List<Card?>) = cardViews.forEachIndexed { i, view ->
        val (image, description) = cardsImageCreator.create(cards.getOrNull(i))
        view.setImageBitmap(image)
        view.contentDescription = description
    }

    // TODO
    private fun updateScores(ourScore: Int, their: Int) {
        updateScore(headerBinding.ourScore, ourScore)
        updateScore(headerBinding.theirScore, their)
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

    private fun initMyCardsHand(myCards: List<Card>) {
        val myPlayingCards = getPlayingCards(myCardsViews, myCards)
        myCardsHand = TrucoSingleOpponentMyCardsHand(myPlayingCards, myDroppingPlacesViews, this@TrucoPlayFor2Fragment)
        loadCardImages(myCardsViews, myCards)
        takeTurn()
    }

    private fun getPlayingCards(cardsViews: List<ImageView>, cards: List<Card>) = cardsViews.mapIndexed { i, view ->
        TrucoCardsHand.PlayingCard(cards[i], view)
    }

    private fun takeTurn() = myCardsHand.takeTurn()

    private fun finishRound(round: Int, result: TrucoRoundResult) {
        gameViewModel.finishRound()
        roundViews[round].animateBackgroundTint(ContextCompat.getColor(requireContext(), result.color)) {
            val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            roundViews.getOrNull(round + 1)?.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        }
    }

    private fun showMyAction(action: TrucoAction) {
        //Show my bubble
        showAction(gameBinding.myActionBubble, gameBinding.myActionBubbleText, action)
    }

    private fun showOpponentAction(action: TrucoAction) {
        //Show their bubble
        showAction(gameBinding.theirActionBubble, gameBinding.theirActionBubbleText, action)
        updateActionAvailableResponses(action.availableResponses())
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

    private fun showActionAfterVisibilityCheck(bubbleBackground: View, bubbleText: TextView, action: TrucoAction) {
        (parentFragmentManager.findFragmentByTag(ACTIONS_BOTTOM_SHEET_TAG) as BottomSheetDialogFragment?)?.dismiss()
        bubbleText.text = action.message(requireContext())
        showBubbleView(bubbleBackground)
        showBubbleView(bubbleText)
        gameBinding.actionBackground.isVisible = true
        gameBinding.actionBackground
            .animate()
            .setListener(null)
            .alpha(ACTION_BACKGROUND_FINAL_ALPHA)
            .start()
        if (!action.hasReplication) {
            bubbleBackground.postDelayed({ hideActions() }, HIDE_ACTION_BUBBLES_DELAY)
        }
    }

    private fun updateActionAvailableResponses(
        availableResponses: TrucoActionAvailableResponses
    ) = with(availableResponses) {
        with(gameBinding.actionsResponses) {
            actionResponseYesIDo.isVisible = iDo
            actionResponseNoIDont.isVisible = iDont
            actionResponseYesEnvido.isVisible = envido
            actionResponseYesRealEnvido.isVisible = realEnvido
            actionResponseYesFaltaEnvido.isVisible = faltaEnvido
            actionResponseYesRetruco.isVisible = retruco
            actionResponseYesValeCuatro.isVisible = valeCuatro
            actionResponseEnvidoGoesFirst.isVisible = envidoGoesFirst
            if (hasAvailableResponses()) actionResponseContainer.fadeIn() else actionResponseContainer.fadeOut()
        }
    }

    private fun showBubbleView(view: View) = view.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(longDuration)
        .setInterpolator(BounceInterpolator())
        .start()

    private fun hideMyActionBubble() {
        hideBubbleView(gameBinding.myActionBubble)
        hideBubbleView(gameBinding.myActionBubbleText)
    }

    private fun hideOpponentActionBubble() {
        hideBubbleView(gameBinding.theirActionBubble)
        hideBubbleView(gameBinding.theirActionBubbleText)
    }

    private fun hideActions() {
        addActionsBottomSheet()
        hideMyActionBubble()
        hideOpponentActionBubble()
        gameBinding.actionBackground.animate()
            .alpha(0f)
            .setOnEndListener { gameBinding.actionBackground.isVisible = false }
            .start()
        gameBinding.actionsResponses.actionResponseContainer.fadeOut()
    }

    private fun hideBubbleView(view: View, onEndListener: () -> Unit = { }) = view.animate()
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(shortDuration)
        .setInterpolator(null)
        .setOnEndListener(onEndListener)
        .start()

    companion object {

        private const val ACTION_BACKGROUND_FINAL_ALPHA = 0.5f
        private const val MIN_ACTION_BUBBLE_VISIBLE_SCALING = 0.9f
        private const val SCORE_ZOOM_ANIMATION = 1.2f
        private const val HIDE_ACTION_BUBBLES_DELAY = 3_000L
        private const val ACTIONS_BOTTOM_SHEET_TAG = "TRUCO_ACTIONS_BOTTOM_SHEET"

        fun newInstance() = TrucoPlayFor2Fragment()
    }
}