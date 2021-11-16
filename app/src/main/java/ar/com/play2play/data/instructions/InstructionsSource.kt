package ar.com.play2play.data.instructions
import ar.com.play2play.presentation.home.games.Game

/** This interface brings the capacity to show instructions for games. */
interface InstructionsSource {

    /** Returns the instructions for a particular [game]. */
    fun getInstructions(game: Game): String
}
