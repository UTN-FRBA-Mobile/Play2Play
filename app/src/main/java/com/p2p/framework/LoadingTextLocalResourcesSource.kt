package com.p2p.framework

import android.content.Context
import com.p2p.R
import com.p2p.data.instructions.InstructionsSource
import com.p2p.data.loadingMessages.LoadingSource
import com.p2p.model.base.message.Message
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.presentation.home.games.Game
import com.p2p.utils.getString

/**Instructions for all games*/
class LoadingTextLocalResourcesSource(private val context: Context) : LoadingSource {
    val textByMessage = mapOf(
        TuttiFruttiEnoughForMeEnoughForAllMessage.TYPE to context.resources.getString(R.string.tf_wait_for_results)
    )

    override fun getLoadingText(messageType: String): String =
        textByMessage[messageType] ?: context.resources.getString(R.string.default_loading_text)

}