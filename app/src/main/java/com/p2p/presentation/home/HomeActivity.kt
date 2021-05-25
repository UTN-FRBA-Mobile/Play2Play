package com.p2p.presentation.home

import android.os.Bundle
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.home.games.GamesFragment

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) { // TODO: should we ask location permission?
            addFragment(GamesFragment.newInstance(), shouldAddToBackStack = true)
            window.setBackgroundDrawableResource(R.color.colorBackground)
        }
    }
}
