package ar.com.play2play.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ar.com.play2play.R
import ar.com.play2play.presentation.base.BaseViewModel

fun Context.hideKeyboard(forView: View? = null) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = forView ?: (if (this is Activity) currentFocus else null) ?: View(this)
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showSnackBar(view: View, data: BaseViewModel.MessageData) {
    val text = data.textRes?.let { resources.getString(it, *data.formatArgs) } ?: data.text ?: return
    val duration = when (data.duration) {
        BaseViewModel.MessageData.Duration.SHORT -> Snackbar.LENGTH_SHORT
        BaseViewModel.MessageData.Duration.LONG -> Snackbar.LENGTH_LONG
    }
    val backgroundColor = when (data.type) {
        BaseViewModel.MessageData.Type.ERROR -> R.color.design_default_color_error
    }
    Snackbar
        .make(view, text, duration)
        .setBackgroundTint(ContextCompat.getColor(this, backgroundColor))
        .show()
}
