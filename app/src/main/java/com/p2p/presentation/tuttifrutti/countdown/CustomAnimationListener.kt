package com.p2p.presentation.tuttifrutti.countdown

import android.view.animation.Animation

interface CustomAnimationListener : Animation.AnimationListener {

    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
}