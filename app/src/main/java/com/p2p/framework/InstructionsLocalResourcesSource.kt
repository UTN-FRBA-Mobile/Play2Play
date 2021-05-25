package com.p2p.framework

import android.content.Context
import com.p2p.data.instructions.InstructionsSource
import com.p2p.presentation.home.games.Game
import com.p2p.utils.getString

/**Instructions for all games*/
class InstructionsLocalResourcesSource(private val context: Context) : InstructionsSource {

    override fun getInstructions(game: Game): String =
        context.resources.openRawResource(game.instructionsRes).getString()


}