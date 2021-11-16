package ar.com.play2play.utils

import androidx.appcompat.app.AppCompatActivity
import ar.com.play2play.presentation.base.BaseViewModel

fun AppCompatActivity.showSnackBar(messageData: BaseViewModel.MessageData) {
    showSnackBar(findViewById(android.R.id.content), messageData)
}
