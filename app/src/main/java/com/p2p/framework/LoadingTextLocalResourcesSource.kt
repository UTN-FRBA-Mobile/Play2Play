package com.p2p.framework

import android.content.Context
import androidx.annotation.StringRes
import com.p2p.R
import com.p2p.data.loadingMessages.LoadingSource
import com.p2p.model.LoadingMessageType

/**Instructions for all games*/
class LoadingTextLocalResourcesSource(context: Context) : LoadingSource {

    private val textByMessage = mapOf(
        LoadingMessageType.TF_WAITING_FOR_REVIEW to getString(context, R.string.tf_wait_for_review),
        LoadingMessageType.TF_WAITING_FOR_WORDS to getString(context, R.string.tf_wait_for_words)
    )

    private fun getString(context: Context, @StringRes stringRes: Int) = context.resources.getString(stringRes)

    override fun getLoadingText(messageType: LoadingMessageType) = textByMessage[messageType].orEmpty()

}