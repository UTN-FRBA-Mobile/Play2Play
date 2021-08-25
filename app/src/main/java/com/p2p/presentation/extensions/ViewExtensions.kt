package com.p2p.presentation.extensions

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible


private const val INVISIBLE_ALPHA = 0f
private const val VISIBLE_ALPHA = 1f

/** Fades out a view. */
internal fun View.fadeOut(
    duration: Long = resources.getInteger(R.integer.config_shortAnimTime).toLong(),
    finalVisibility: Int = View.GONE,
    onComplete: () -> Unit = {}
) {
    if (!isVisible || alpha == INVISIBLE_ALPHA) {
        return onComplete()
    }

    animate()
        .alpha(INVISIBLE_ALPHA)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                this@fadeOut.visibility = finalVisibility
                onComplete()
            }
        })
        .start()
}

/** Fades in a view. */
internal fun View.fadeIn(
    duration: Long = resources.getInteger(R.integer.config_shortAnimTime).toLong(),
    onComplete: () -> Unit = {}
) {
    if (isVisible && alpha == VISIBLE_ALPHA) {
        return onComplete()
    } else if (!isVisible) {
        alpha = INVISIBLE_ALPHA
        isVisible = true
    }

    animate()
        .alpha(VISIBLE_ALPHA)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onComplete()
            }
        })
        .start()
}

fun View.animateBackgrondTint(toColor: Int, onComplete: () -> Unit) {
    val fromColor = backgroundTintList?.defaultColor ?: Color.WHITE
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        .setDuration(resources.getInteger(R.integer.config_mediumAnimTime).toLong())
        .apply {
            addUpdateListener { animator -> backgroundTintList = ColorStateList.valueOf(animator.animatedValue as Int) }
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) = onComplete()
            })
        }
        .start()
}