package ar.com.play2play.framework

import android.content.Context
import androidx.annotation.StringRes
import ar.com.play2play.R
import ar.com.play2play.data.loadingMessages.LoadingSource
import ar.com.play2play.model.LoadingMessageType

/**Instructions for all games*/
class LoadingTextLocalResourcesSource(context: Context) : LoadingSource {

    private val textByMessage = mapOf(
        LoadingMessageType.TF_WAITING_FOR_REVIEW to getString(context, R.string.tf_wait_for_review),
        LoadingMessageType.TF_WAITING_FOR_WORDS to getString(context, R.string.tf_wait_for_words)
    )

    override fun getLoadingText(messageType: LoadingMessageType) = textByMessage[messageType].orEmpty()

    private fun getString(context: Context, @StringRes stringRes: Int) = context.resources.getString(stringRes)
}