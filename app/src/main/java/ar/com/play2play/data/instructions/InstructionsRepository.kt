package ar.com.play2play.data.instructions

import ar.com.play2play.presentation.home.games.Game

data class InstructionsRepository(private val instructionsSource: InstructionsSource) {

    fun getInstructions(game: Game): String = instructionsSource.getInstructions(game)
}