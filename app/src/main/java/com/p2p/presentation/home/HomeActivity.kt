package com.p2p.presentation.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.home.games.GamesFragment

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(GamesFragment.newInstance(), shouldAddToBackStack = false)
            removeSplashStyle()
        }

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION), 1);
        }
    }

    private fun removeSplashStyle() = with(window) {
        setBackgroundDrawableResource(R.color.colorBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}
