package com.p2p.presentation.tuttifrutti.finalscore

data class FinalScore(val score: List<Score>)

data class Score(val player: String, val points: Int)