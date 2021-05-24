package com.p2p.presentation.countdown

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.p2p.R
import com.p2p.presentation.base.BaseActivity


class CountdownActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown)

        val firstCountdownElement : ImageView = findViewById(R.id.countdown_3)
        val secondCountdownElement : ImageView = findViewById(R.id.countdown_2)
        val thirdCountdownElement : ImageView= findViewById(R.id.countdown_1)

        val firstZoomInOutAnimation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in_out)
        val secondZoomInOutAnimation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in_out)
        val thirdZoomInOutAnimation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in_out)

        secondZoomInOutAnimation.startOffset = 1500
        thirdZoomInOutAnimation.startOffset = 3000

        val countdownElements: List<ImageView> =
            listOf(firstCountdownElement, secondCountdownElement, thirdCountdownElement)
        val animationElements: List<Animation> =
            listOf(firstZoomInOutAnimation, secondZoomInOutAnimation, thirdZoomInOutAnimation)

        startCountdown(countdownElements, animationElements)

        startGame()
    }

    private fun startCountdown(countdownElements: List<ImageView>, animationElements: List<Animation>) {
        countdownElements.zip(animationElements).forEach { pair ->
            val countdownElement = pair.component1()
            val animationElement = pair.component2()
            countdownElement.visibility = View.VISIBLE
            countdownElement.startAnimation(animationElement)
            countdownElement.visibility = View.INVISIBLE
        }
    }

    private fun startGame() {
        // TODO: Start the corresponding Game
    }

}
