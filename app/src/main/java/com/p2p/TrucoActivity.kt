package com.p2p

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.p2p.model.truco.Card
import com.p2p.model.truco.Suit
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.truco.CardImageCreator
import com.p2p.presentation.truco.TrucoCardsHand

class TrucoActivity : BaseActivity(R.layout.activity_truco) {

    lateinit var trucoCardsHand: TrucoCardsHand

    private val cardsImageCreator by lazy { CardImageCreator(baseContext) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val suits = listOf(Suit.SWORDS, Suit.GOLDS, Suit.CUPS, Suit.CLUBS)
        val numbers: List<Int> = (1..7).plus(10..12)
        val cards = suits.flatMap { suit -> numbers.map { number -> Card(number, suit) } }.shuffled()
        findViewById<ImageView>(R.id.left_card).setImageBitmap(cardsImageCreator.create(cards[0]))
        findViewById<ImageView>(R.id.middle_card).setImageBitmap(cardsImageCreator.create(cards[1]))
        findViewById<ImageView>(R.id.right_card).setImageBitmap(cardsImageCreator.create(cards[2]))
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

                override fun onCardPlayed(card: TrucoCardsHand.PlayingCard) {
                    Toast.makeText(baseContext, "Se jugó la carta ${card.card}", Toast.LENGTH_LONG).show()
                    card.view.postDelayed({ trucoCardsHand.takeTurn() }, 2_000)
                }
            }
        )

        trucoCardsHand.takeTurn()
    }
}