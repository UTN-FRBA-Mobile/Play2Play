package com.p2p.framework

import android.content.Context
import com.p2p.data.InstructionsSource
import com.p2p.presentation.extensions.getString
import com.p2p.presentation.home.games.Game

/**Instructions for all games*/
class InstructionsLocalResourcesSource(private val context: Context) : InstructionsSource {

    private val instructionsByGame = mapOf(
        Game.TUTTI_FRUTTI to getForGame(Game.TUTTI_FRUTTI.instructionsRes)
    )

    private fun getForGame(resourceId: Int): String =
        context.resources.openRawResource(resourceId).getString()

    override fun instructionsByGame(): Map<Game, String> =
        instructionsByGame


}