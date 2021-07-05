package com.p2p.utils

import androidx.appcompat.app.AppCompatActivity
import com.p2p.presentation.base.BaseViewModel

fun AppCompatActivity.showSnackBar(messageData: BaseViewModel.MessageData) {
    showSnackBar(findViewById(android.R.id.content), messageData)
}
