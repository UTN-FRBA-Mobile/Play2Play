package com.p2p

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.p2p.databinding.ActivityTrucoBinding
import com.p2p.databinding.ViewTrucoHeaderBinding
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.extensions.animateBackgrondTint
import com.p2p.presentation.extensions.fadeIn
import com.p2p.presentation.extensions.fadeOut
import com.p2p.presentation.truco.actions.TrucoAction
import com.p2p.presentation.truco.actions.TrucoActionsBottomSheetFragment
import com.p2p.presentation.truco.cards.CardImageCreator
import com.p2p.presentation.truco.cards.TrucoCardsHand
import com.p2p.presentation.truco.cards.TrucoSingleOpponentMyCardsHand
import com.p2p.presentation.truco.cards.TrucoSingleOpponentTheirCardsHand
import com.p2p.utils.setOnEndListener
import kotlin.random.Random

// TODO: this is just a test activity, remove it
class TrucoActivity : BaseActivity(0) {

    private lateinit var binding: ActivityTrucoBinding
    private lateinit var headerBinding: ViewTrucoHeaderBinding
    private lateinit var myCardsHand: TrucoCardsHand
    private lateinit var theirCardsHand: TrucoCardsHand

    private val cardsImageCreator by lazy { CardImageCreator(baseContext) }
    private lateinit var roundViews: List<View>
    private lateinit var myCardsViews: List<ImageView>
    private lateinit var theirCardsViews: List<ImageView>
    private lateinit var dropCardsViews: List<View>
    private lateinit var theirDroppingPlacesViews: List<View>

    private val shortDuration by lazy { resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }
    private val longDuration by lazy { resources.getInteger(android.R.integer.config_longAnimTime).toLong() }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrucoBinding.inflate(layoutInflater)
        headerBinding = ViewTrucoHeaderBinding.bind(binding.root)
        setContentView(binding.root)
        roundViews = listOf(headerBinding.firstRound, headerBinding.secondRound, headerBinding.thirdRound)
        myCardsViews = listOf(binding.myLeftCard, binding.myMiddleCard, binding.myRightCard)
        theirCardsViews = listOf(binding.theirLeftCard, binding.theirMiddleCard, binding.theirRightCard)
        dropCardsViews = listOf(binding.dropFirstCard, binding.dropSecondCard, binding.dropThirdCard)
        theirDroppingPlacesViews =
            listOf(binding.dropTheirFirstCard, binding.dropTheirSecondCard, binding.dropTheirThirdCard)

        val suits = listOf(Suit.SWORDS, Suit.GOLDS, Suit.CUPS, Suit.CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        val cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
        myCardsViews.forEachIndexed { index, view ->
            val (image, description) = cardsImageCreator.create(cards[index])
            view.setImageBitmap(image)
            view.contentDescription = description
        }
        theirCardsViews.forEachIndexed { index, view ->
            val (image, description) = cardsImageCreator.create(null)
            view.setImageBitmap(image)
            view.contentDescription = description
        }
        updateScores(0, 0)
        var currentRound = 0
        myCardsHand = TrucoSingleOpponentMyCardsHand(
            myCardsViews.mapIndexed { index, view -> TrucoCardsHand.PlayingCard(cards[index], view) },
            dropCardsViews,
            object : TrucoCardsHand.Listener {

                override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {
                    val opponentCard = cards[currentRound + 3]
                    theirCardsHand.playCard(
                        opponentCard,
                        cardsImageCreator.create(opponentCard),
                        theirDroppingPlacesViews[currentRound]
                    )
                    when (currentRound) {
                        0 -> showOpponentAction(TrucoAction.Envido(false))
                        1 -> showOpponentAction(TrucoAction.Truco)
                    }
                    playingCard.view.postDelayed({ myCardsHand.takeTurn() }, 2_000)
                    updateScores(Random.nextInt(1, 4), Random.nextInt(4, 10))
                    finishRound(
                        currentRound,
                        when (currentRound) {
                            0 -> TrucoRoundResult.WIN
                            1 -> TrucoRoundResult.DEFEAT
                            else -> TrucoRoundResult.TIE
                        }
                    )
                    currentRound++
                }
            }
        )
        theirCardsHand = TrucoSingleOpponentTheirCardsHand(
            theirCardsViews.mapIndexed { index, view -> TrucoCardsHand.PlayingCard(cards[index + 3], view) }
        )
        if (savedInstanceState == null) {
            addActionsBottomSheet()
        }
        myCardsHand.takeTurn()

        binding.actionsResponses.actionResponseYesIDo.setOnClickListener { replyAction(TrucoAction.YesIDo) }
        binding.actionsResponses.actionResponseNoIDont.setOnClickListener { replyAction(TrucoAction.NoIDont) }
        binding.actionsResponses.actionResponseYesEnvido.setOnClickListener { replyAction(TrucoAction.Envido(true)) }
        binding.actionsResponses.actionResponseYesRealEnvido.setOnClickListener { replyAction(TrucoAction.RealEnvido) }
        binding.actionsResponses.actionResponseYesFaltaEnvido.setOnClickListener { replyAction(TrucoAction.FaltaEnvido) }
        binding.actionsResponses.actionResponseYesRetruco.setOnClickListener { replyAction(TrucoAction.Retruco) }
        binding.actionsResponses.actionResponseYesValeCuatro.setOnClickListener { replyAction(TrucoAction.ValeCuatro) }
    }

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

    private fun finishRound(round: Int, result: TrucoRoundResult) {
        val color = when (result) {
            TrucoRoundResult.WIN -> R.color.colorSuccess
            TrucoRoundResult.TIE -> R.color.colorWarning
            TrucoRoundResult.DEFEAT -> R.color.colorError
        }
        roundViews[round].animateBackgrondTint(ContextCompat.getColor(baseContext, color)) {
            val colorPrimary = ContextCompat.getColor(baseContext, R.color.colorPrimary)
            roundViews.getOrNull(round + 1)?.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        }
    }

    fun showMyAction(action: TrucoAction) {
        showAction(binding.myActionBubble, binding.myActionBubbleText, action)

        // TODO: remove it, just for test
        if (action in listOf(
                TrucoAction.Truco,
                TrucoAction.Retruco,
                TrucoAction.ValeCuatro
            ) || action.javaClass.simpleName.contains("envido", ignoreCase = true)
        ) {
            binding.myActionBubbleText.postDelayed(
                {
                    showOpponentAction(
                        when (action) {
                            TrucoAction.Truco -> TrucoAction.Retruco
                            TrucoAction.Retruco -> TrucoAction.ValeCuatro
                            TrucoAction.ValeCuatro -> TrucoAction.NoIDont
                            else -> TrucoAction.CustomFinalActionResponse("Quiero,\n27", hasReplication = true)
                        }
                    )
                },
                2_000
            )
        }
    }

    fun showOpponentAction(action: TrucoAction) {
        showAction(binding.theirActionBubble, binding.theirActionBubbleText, action)
        updateActionAvaileResponses(action)

        // TODO: remove it, just for test
        if (action.getMessage(baseContext) == "Quiero,\n27") {
            binding.myActionBubbleText.postDelayed(
                { showMyAction(TrucoAction.CustomFinalActionResponse("31 son\nmejores")) },
                2_000
            )
        }
    }

    private fun replyAction(action: TrucoAction) {
        showMyAction(action)
        binding.actionsResponses.actionResponseContainer.fadeOut()
    }

    private fun showAction(
        bubbleBackground: View,
        bubbleText: TextView,
        action: TrucoAction,
    ) {
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
        (supportFragmentManager.findFragmentByTag(ACTIONS_BOTTOM_SHEET_TAG) as BottomSheetDialogFragment?)?.dismiss()
        bubbleText.text = action.getMessage(baseContext)
        showBubbleView(bubbleBackground)
        showBubbleView(bubbleText)
        binding.actionBackground.isVisible = true
        binding.actionBackground.animate()
            .setListener(null)
            .alpha(ACTION_BACKGROUND_FINAL_ALPHA)
            .start()
        if (!action.hasReplication) {
            bubbleBackground.postDelayed({ hideActions() }, HIDE_ACTION_BUBBLES_DELAY)
        }
    }

    private fun updateActionAvaileResponses(action: TrucoAction) = with(action.getAvailableResponses()) {
        binding.actionsResponses.actionResponseYesIDo.isVisible = iDo
        binding.actionsResponses.actionResponseNoIDont.isVisible = iDont
        binding.actionsResponses.actionResponseYesEnvido.isVisible = envido
        binding.actionsResponses.actionResponseYesRealEnvido.isVisible = realEnvido
        binding.actionsResponses.actionResponseYesFaltaEnvido.isVisible = faltaEnvido
        binding.actionsResponses.actionResponseYesRetruco.isVisible = retruco
        binding.actionsResponses.actionResponseYesValeCuatro.isVisible = valeCuatro
        binding.actionsResponses.actionResponseContainer.fadeIn()
    }

    private fun showBubbleView(view: View) = view.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(longDuration)
        .setInterpolator(BounceInterpolator())
        .start()

    private fun hideMyActionBubble() {
        hideBubbleView(binding.myActionBubble)
        hideBubbleView(binding.myActionBubbleText)
    }

    private fun hideOpponentActionBubble() {
        hideBubbleView(binding.theirActionBubble)
        hideBubbleView(binding.theirActionBubbleText)
    }

    private fun hideActions() {
        addActionsBottomSheet()
        hideMyActionBubble()
        hideOpponentActionBubble()
        binding.actionBackground.animate()
            .alpha(0f)
            .setOnEndListener { binding.actionBackground.isVisible = false }
            .start()
        binding.actionsResponses.actionResponseContainer.fadeOut()
    }

    private fun hideBubbleView(view: View, onEndListener: () -> Unit = { }) = view.animate()
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(shortDuration)
        .setInterpolator(null)
        .setOnEndListener(onEndListener)
        .start()

    private fun addActionsBottomSheet() = TrucoActionsBottomSheetFragment
        .newInstance()
        .show(supportFragmentManager, ACTIONS_BOTTOM_SHEET_TAG)

    enum class TrucoRoundResult {
        WIN,
        TIE,
        DEFEAT
    }

    companion object {
        private const val SCORE_ZOOM_ANIMATION = 1.2f
        private const val ACTION_BACKGROUND_FINAL_ALPHA = 0.5f
        private const val MIN_ACTION_BUBBLE_VISIBLE_SCALING = 0.9f
        private const val HIDE_ACTION_BUBBLES_DELAY = 3_000L
        private const val ACTIONS_BOTTOM_SHEET_TAG = "TRUCO_ACTIONS_BOTTOM_SHEET"
    }
}
