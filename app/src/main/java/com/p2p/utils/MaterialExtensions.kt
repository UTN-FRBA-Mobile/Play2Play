package com.p2p.utils

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.p2p.R
import com.p2p.presentation.base.BaseViewModel

fun TextInputLayout.text(): String = this.editText?.text.toString()

fun TextInputLayout.clear() {
    this.editText?.run {
        setText("")
        isErrorEnabled = false
    }
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