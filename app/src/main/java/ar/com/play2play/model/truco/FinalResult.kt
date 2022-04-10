package ar.com.play2play.model.truco

data class FinalResult(
    val isWinner: Boolean,
    val ourScore: Int,
    val theirScore: Int
)
