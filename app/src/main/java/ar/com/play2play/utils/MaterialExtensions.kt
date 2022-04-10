package ar.com.play2play.utils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.text(): String = this.editText?.text.toString()

fun TextInputLayout.clear() {
    this.editText?.run {
        setText("")
        isErrorEnabled = false
    }
}

fun TextInputEditText.value(): String = this.text.toString()
