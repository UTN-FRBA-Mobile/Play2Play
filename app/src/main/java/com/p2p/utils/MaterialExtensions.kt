package com.p2p.utils

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.text(): String = this.editText?.text.toString()

fun TextInputLayout.clear(){
    this.editText?.setText("")
}