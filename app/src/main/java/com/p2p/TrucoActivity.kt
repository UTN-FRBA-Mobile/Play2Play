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
import com.p2p.presentation.extensions.animateBackgrondTint
import com.p2p.presentation.truco.TrucoActionsBottomSheetFragment
import com.p2p.presentation.truco.cards.CardImageCreator
import com.p2p.presentation.truco.cards.TrucoCardsHand
import kotlin.random.Random

class TrucoActivity : BaseActivity(R.layout.activity_truco) {

    lateinit var trucoCardsHand: TrucoCardsHand

    private val cardsImageCreator by lazy { CardImageCreator(baseContext) }
    val rounds by lazy {
        listOf<View>(
            findViewById(R.id.first_round),
            findViewById(R.id.second_round),
            findViewById(R.id.third_round)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val suits = listOf(Suit.SWORDS, Suit.GOLDS, Suit.CUPS, Suit.CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        val cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
        findViewById<ImageView>(R.id.left_card).setImageBitmap(cardsImageCreator.create(cards[0]))
        findViewById<ImageView>(R.id.middle_card).setImageBitmap(cardsImageCreator.create(cards[1]))
        findViewById<ImageView>(R.id.right_card).setImageBitmap(cardsImageCreator.create(cards[2]))
        updateScores(0, 0)
        var currentRound = 0
        trucoCardsHand = TrucoCardsHand(
            listOf(
                TrucoCardsHand.PlayingCard(cards[0], findViewById(R.id.left_card)),
                TrucoCardsHand.PlayingCard(cards[1], findViewById(R.id.middle_card)),
                TrucoCardsHand.PlayingCard(cards[2], findViewById(R.id.right_card))
            ),
            listOf<View>(
                findViewById(R.id.drop_first_card),
                findViewById(R.id.drop_second_card),
                findViewById(R.id.drop_third_card)
            ),
            object : TrucoCardsHand.Listener {

                override fun onCardPlayed(playingCard: TrucoCardsHand.PlayingCard) {
                    Toast.makeText(baseContext, "Se jugó la carta ${playingCard.card}", Toast.LENGTH_LONG).show()
                    playingCard.view.postDelayed({ trucoCardsHand.takeTurn() }, 2_000)
                    updateScores(Random.nextInt(1, 4), Random.nextInt(4, 10))
                    finishRound(
                        currentRound,
                        when (currentRound) {
                            0 -> TrucoTurnResult.WIN
                            1 -> TrucoTurnResult.DEFEAT
                            else -> TrucoTurnResult.TIE
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
            .scaleX(1.3f)
            .scaleY(1.3f)
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

    private fun finishRound(round: Int, result: TrucoTurnResult) {
        val currentRoundView = rounds[round]
        val color = when (result) {
            TrucoTurnResult.WIN -> R.color.colorSuccess
            TrucoTurnResult.TIE -> R.color.colorWarning
            TrucoTurnResult.DEFEAT -> R.color.colorError
        }
        currentRoundView.animateBackgrondTint(ContextCompat.getColor(baseContext, color)) {
            val colorPrimary = ContextCompat.getColor(baseContext, R.color.colorPrimary)
            rounds.getOrNull(round + 1)?.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        }
    }

    enum class TrucoTurnResult {
        WIN,
        TIE,
        DEFEAT
    }
}
