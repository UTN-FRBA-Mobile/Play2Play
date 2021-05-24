package com.p2p.data.instructions
import com.p2p.presentation.home.games.Game

/** This interface brings the capacity to instructions for games. */
interface InstructionsSource {

    /** Returns the instructions for a particular [game]. */
    fun getInstructions(game: Game): String
}
