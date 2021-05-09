package com.p2p.presentation.extensions

import android.widget.EditText

/** Clears the text and append a new one. It's useful to keep the cursor at the end of the input. */
fun EditText.clearAndAppend(text: String?) {
    setText(null)
    append(text)
}
