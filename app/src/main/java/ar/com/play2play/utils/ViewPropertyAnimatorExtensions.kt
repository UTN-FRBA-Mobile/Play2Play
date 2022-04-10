package ar.com.play2play.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.ViewPropertyAnimator

fun ViewPropertyAnimator.setOnEndListener(action: () -> Unit): ViewPropertyAnimator =
    setListener(object : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator?) = action()
    })
