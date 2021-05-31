package com.p2p.presentation.home

import android.os.Bundle
import android.view.WindowManager
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.home.games.GamesFragment

// TODO: should we ask ACCESS_COARSE_LOCATION permission on runtime? on some examples they do, maybe it's the reason
//  of why the discovering isn't working, but we didn't investigate it so much.
class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(GamesFragment.newInstance(), shouldAddToBackStack = false)
            removeSplashStyle()
        }
    }

    private fun removeSplashStyle() = with(window) {
        setBackgroundDrawableResource(R.color.colorBackground)
        clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
