package ar.com.play2play.framework

import android.content.Context
import ar.com.play2play.data.instructions.InstructionsSource
import ar.com.play2play.presentation.home.games.Game
import ar.com.play2play.utils.getString

/**Instructions for all games*/
class InstructionsLocalResourcesSource(private val context: Context) : InstructionsSource {

    override fun getInstructions(game: Game): String =
        context.resources.openRawResource(game.instructionsRes).getString()


}