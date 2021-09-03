package com.p2p

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.extensions.animateBackgroundTint
import com.p2p.presentation.truco.TrucoActionsBottomSheetFragment
import com.p2p.presentation.truco.cards.CardImageCreator
import com.p2p.presentation.truco.cards.TrucoCardsHand
import kotlin.random.Random

// TODO: this is just a test activity, remove it
class TrucoActivity : BaseActivity(R.layout.activity_truco) {

    lateinit var trucoCardsHand: TrucoCardsHand

    private val cardsImageCreator by lazy { CardImageCreator(baseContext) }
    private lateinit var roundViews: List<View>
    private lateinit var cardViews: List<ImageView>
    private lateinit var dropCardsViews: List<View>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roundViews = listOf<View>(
            findViewById(R.id.first_round),
            findViewById(R.id.second_round),
            findViewById(R.id.third_round)
        )
        cardViews = listOf<ImageView>(
            findViewById(R.id.left_card),
            findViewById(R.id.middle_card),
            findViewById(R.id.right_card)
        )
        dropCardsViews = listOf<View>(
            findViewById(R.id.drop_first_card),
            findViewById(R.id.drop_second_card),
            findViewById(R.id.drop_third_card)
        )

        val suits = listOf(Suit.SWORDS, Suit.GOLDS, Suit.CUPS, Suit.CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        val cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
        cardViews.forEachIndexed { index, view ->
            val (image, description) = cardsImageCreator.create(cards[index])
            view.setImageBitmap(image)
            view.contentDescription = description
        }
        updateScores(0, 0)
        var currentRound = 0
        trucoCardsHand = TrucoCardsHand(
            cardViews.mapIndexed { index, view -> TrucoCardsHand.PlayingCard(cards[index], view) },
            dropCardsViews,
            object : TrucoCardsHand.Listener {

                override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {
                    Toast.makeText(baseContext, "Se jugó la carta ${playingCard.card}", Toast.LENGTH_LONG).show()
                    playingCard.view.postDelayed({ trucoCardsHand.takeTurn() }, 2_000)
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
        if (savedInstanceState == null) {
            TrucoActionsBottomSheetFragment().show(supportFragmentManager, null)
        }
        trucoCardsHand.takeTurn()
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
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    textView.text = score.toString()
                    textView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                }
            })
            .start()
    }

    private fun finishRound(round: Int, result: TrucoRoundResult) {
        val color = when (result) {
            TrucoRoundResult.WIN -> R.color.colorSuccess
            TrucoRoundResult.TIE -> R.color.colorWarning
            TrucoRoundResult.DEFEAT -> R.color.colorError
        }
        roundViews[round].animateBackgroundTint(ContextCompat.getColor(baseContext, color)) {
            val colorPrimary = ContextCompat.getColor(baseContext, R.color.colorPrimary)
            roundViews.getOrNull(round + 1)?.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        }
    }

    enum class TrucoRoundResult {
        WIN,
        TIE,
        DEFEAT
    }

    companion object {
        private const val SCORE_ZOOM_ANIMATION = 1.2f
    }
}
