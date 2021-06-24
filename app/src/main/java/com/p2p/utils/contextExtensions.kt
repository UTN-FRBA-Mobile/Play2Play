package com.p2p.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.hideKeyboard(forView: View? = null) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = forView ?: (if (this is Activity) currentFocus else null) ?: View(this)
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
