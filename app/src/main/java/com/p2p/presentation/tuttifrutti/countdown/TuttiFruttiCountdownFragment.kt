package com.p2p.presentation.tuttifrutti.countdown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.p2p.R
import com.p2p.presentation.tuttifrutti.play.PlayTuttiFruttiFragment

class TuttiFruttiCountdownFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutti_frutti_countdown, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val applicationContext = requireContext()

        val countdownView = view.findViewById<ImageView>(R.id.countdown)

        val firstZoomInOutAnimation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in_out)
        val secondZoomInOutAnimation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in_out)
        val thirdZoomInOutAnimation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in_out)

        firstZoomInOutAnimation.setAnimationListener(object : CustomAnimationListener {
             override fun onAnimationEnd(animation: Animation) {
                countdownView.setImageResource(R.drawable.ic_countdown_2)
                countdownView.startAnimation(secondZoomInOutAnimation)
            }
        })

        secondZoomInOutAnimation.setAnimationListener(object : CustomAnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                countdownView.setImageResource(R.drawable.ic_countdown_1)
                countdownView.startAnimation(thirdZoomInOutAnimation)
            }
        })

        thirdZoomInOutAnimation.setAnimationListener(object : CustomAnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                countdownView.visibility = View.INVISIBLE
                startGame()
            }
        })

        // We start the countdown animation
        countdownView.startAnimation(firstZoomInOutAnimation)
    }

    private fun startGame() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container_view,
                PlayTuttiFruttiFragment.newInstance())
        }
    }

    companion object {

        /** Create a new instance of the [TuttiFruttiCountdownFragment]. */
        fun newInstance() =
            TuttiFruttiCountdownFragment()
    }
}