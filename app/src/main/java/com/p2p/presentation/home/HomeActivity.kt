package com.p2p.presentation.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.home.games.GamesFragment

class HomeActivity : BaseActivity() {

    private var hasShownLocationExplanation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(GamesFragment.newInstance(), shouldAddToBackStack = false)
            removeSplashStyle()
        }

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE && grantResults.firstOrNull() != PERMISSION_GRANTED) {
            if (!hasShownLocationExplanation) {
                hasShownLocationExplanation = true
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.request_location_title)
                    .setMessage(R.string.request_location_description)
                    .setPositiveButton(R.string.understood) { _, _ -> requestLocationPermission() }
                    .show()
            } else {
                finish() // If the user doesn't give us the location permission we close the application :(
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION_CODE)
    }

    private fun removeSplashStyle() = with(window) {
        setBackgroundDrawableResource(R.color.colorBackground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    companion object {

        private const val REQUEST_LOCATION_PERMISSION_CODE = 1001
    }
}
