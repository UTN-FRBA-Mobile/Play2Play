package com.p2p

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
class TrucoActivity : BaseActivity(R.layout.activity_truco) {

    private lateinit var myCardsHand: TrucoCardsHand
    private lateinit var theirCardsHand: TrucoCardsHand

    private lateinit var actionBackground: View
    private lateinit var myActionBubble: View
    private lateinit var myActionBubbleText: TextView
    private lateinit var theirActionBubble: View
    private lateinit var theirActionBubbleText: TextView

    private lateinit var actionResponseContainer: ViewGroup
    private lateinit var actionResponseYesIDo: Button
    private lateinit var actionResponseEnvido: Button
    private lateinit var actionResponseRealEnvido: Button
    private lateinit var actionResponseFaltaEnvido: Button
    private lateinit var actionResponseRetruco: Button
    private lateinit var actionResponseValeCuatro: Button
    private lateinit var actionResponseNoIDont: Button

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
        roundViews = listOf<View>(
            findViewById(R.id.first_round),
            findViewById(R.id.second_round),
            findViewById(R.id.third_round)
        )
        myCardsViews = listOf<ImageView>(
            findViewById(R.id.my_left_card),
            findViewById(R.id.my_middle_card),
            findViewById(R.id.my_right_card)
        )
        theirCardsViews = listOf<ImageView>(
            findViewById(R.id.their_left_card),
            findViewById(R.id.their_middle_card),
            findViewById(R.id.their_right_card)
        )
        dropCardsViews = listOf<View>(
            findViewById(R.id.drop_first_card),
            findViewById(R.id.drop_second_card),
            findViewById(R.id.drop_third_card)
        )
        theirDroppingPlacesViews = listOf<View>(
            findViewById(R.id.drop_their_first_card),
            findViewById(R.id.drop_their_second_card),
            findViewById(R.id.drop_their_third_card)
        )
        actionBackground = findViewById(R.id.action_background)
        myActionBubble = findViewById(R.id.my_action_bubble)
        myActionBubbleText = findViewById(R.id.my_action_bubble_text)
        theirActionBubble = findViewById(R.id.their_action_bubble)
        theirActionBubbleText = findViewById(R.id.their_action_bubble_text)
        actionResponseYesIDo = findViewById(R.id.action_response_yes_i_do)
        actionResponseEnvido = findViewById(R.id.action_response_yes_envido)
        actionResponseRealEnvido = findViewById(R.id.action_response_yes_real_envido)
        actionResponseFaltaEnvido = findViewById(R.id.action_response_yes_falta_envido)
        actionResponseRetruco = findViewById(R.id.action_response_yes_retruco)
        actionResponseValeCuatro = findViewById(R.id.action_response_yes_vale_cuatro)
        actionResponseNoIDont = findViewById(R.id.action_response_no_i_dont)
        actionResponseContainer = findViewById(R.id.action_response_container)

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

        actionResponseYesIDo.setOnClickListener { replyAction(TrucoAction.YesIDo) }
        actionResponseNoIDont.setOnClickListener { replyAction(TrucoAction.NoIDont) }
        actionResponseEnvido.setOnClickListener { replyAction(TrucoAction.Envido(true)) }
        actionResponseRealEnvido.setOnClickListener { replyAction(TrucoAction.RealEnvido) }
        actionResponseFaltaEnvido.setOnClickListener { replyAction(TrucoAction.FaltaEnvido) }
        actionResponseRetruco.setOnClickListener { replyAction(TrucoAction.Retruco) }
        actionResponseValeCuatro.setOnClickListener { replyAction(TrucoAction.ValeCuatro) }
    }

    private fun updateScores(ourScore: Int, their: Int) {
        updateScore(findViewById(R.id.our_score), ourScore)
        updateScore(findViewById(R.id.their_score), their)
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
        showAction(myActionBubble, myActionBubbleText, action)

        // TODO: remove it, just for test
        if (action in listOf(
                TrucoAction.Truco,
                TrucoAction.Retruco,
                TrucoAction.ValeCuatro
            ) || action.javaClass.simpleName.contains("envido", ignoreCase = true)
        ) {
            myActionBubbleText.postDelayed(
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
        showAction(theirActionBubble, theirActionBubbleText, action)
        updateActionAvaileResponses(action)

        // TODO: remove it, just for test
        if (action.getMessage(baseContext) == "Quiero,\n27") {
            myActionBubbleText.postDelayed(
                { showMyAction(TrucoAction.CustomFinalActionResponse("31 son\nmejores")) },
                2_000
            )
        }
    }

    private fun replyAction(action: TrucoAction) {
        showMyAction(action)
        actionResponseContainer.fadeOut()
    }

    private fun showAction(
        bubbleBackground: View,
        bubbleText: TextView,
        action: TrucoAction,
    ) {
        if (bubbleBackground.scaleX >= 0.95f) {
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
        actionBackground.isVisible = true
        actionBackground.animate()
            .setListener(null)
            .alpha(0.5f)
            .start()
        if (!action.hasReplication) {
            bubbleBackground.postDelayed({ hideActions() }, 2_000)
        }
    }

    private fun updateActionAvaileResponses(action: TrucoAction) = with(action.getAvailableResponses()) {
        actionResponseYesIDo.isVisible = iDo
        actionResponseNoIDont.isVisible = iDont
        actionResponseEnvido.isVisible = envido
        actionResponseRealEnvido.isVisible = realEnvido
        actionResponseFaltaEnvido.isVisible = faltaEnvido
        actionResponseRetruco.isVisible = retruco
        actionResponseValeCuatro.isVisible = valeCuatro
        actionResponseContainer.fadeIn()
    }

    private fun showBubbleView(view: View) = view.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(longDuration)
        .setInterpolator(BounceInterpolator())
        .start()

    private fun hideMyActionBubble() {
        hideBubbleView(myActionBubble)
        hideBubbleView(myActionBubbleText)
    }

    private fun hideOpponentActionBubble() {
        hideBubbleView(theirActionBubble)
        hideBubbleView(theirActionBubbleText)
    }

    private fun hideActions() {
        addActionsBottomSheet()
        hideMyActionBubble()
        hideOpponentActionBubble()
        actionBackground.animate()
            .alpha(0f)
            .setOnEndListener { actionBackground.isVisible = false }
            .start()
        actionResponseContainer.fadeOut()
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
        private const val ACTIONS_BOTTOM_SHEET_TAG = "TRUCO_ACTIONS_BOTTOM_SHEET"
    }
}
