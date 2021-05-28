package com.p2p.data.instructions

import com.p2p.presentation.home.games.Game

data class InstructionsRepository(private val instructionsSource: InstructionsSource) {

    fun getInstructions(game: Game): String = instructionsSource.getInstructions(game)
}