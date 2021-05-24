package com.p2p.data
import com.p2p.presentation.home.games.Game

/** This interface brings the capacity to instructions for games. */
interface InstructionsSource {

    /** Returns the instructions for all games. */
    fun instructionsByGame(): Map<Game, String>
}
