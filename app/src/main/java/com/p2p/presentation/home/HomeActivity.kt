package com.p2p.presentation.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.base.BaseViewModel
import com.p2p.presentation.basegame.GameActivity
import com.p2p.presentation.home.games.GamesFragment
import com.p2p.utils.showSnackBar

class HomeActivity : BaseActivity() {

    private var hasShownLocationExplanation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(GamesFragment.newInstance(), shouldAddToBackStack = false)
            removeSplashStyle()
        }
        if (!hasLocationPermissions()) requestLocationPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE && grantResults.any { it != PERMISSION_GRANTED }) {
            if (!hasShownLocationExplanation) {
                hasShownLocationExplanation = true
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.request_location_title)
                    .setMessage(R.string.request_location_description)
                    .setPositiveButton(R.string.understood) { _, _ -> requestLocationPermissions() }
                    .show()
            } else {
                finish() // If the user doesn't give us the location permission we close the application :(
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GAME_REQUEST_CODE && resultCode == GameActivity.RESULT_ERROR_BLUETOOTH_OFF) {
            showSnackBar(
                BaseViewModel.MessageData(
                    textRes = R.string.error_bluetooth_off,
                    type = BaseViewModel.MessageData.Type.ERROR
                )
            )
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }


    private fun requestLocationPermissions() = ActivityCompat.requestPermissions(
        this,
        arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_PERMISSION_CODE
    )

    private fun removeSplashStyle() = with(window) {
        setBackgroundDrawableResource(R.color.colorBackground)
        clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    companion object {

        const val GAME_REQUEST_CODE = 9001
        private const val REQUEST_LOCATION_PERMISSION_CODE = 1001
    }
}
