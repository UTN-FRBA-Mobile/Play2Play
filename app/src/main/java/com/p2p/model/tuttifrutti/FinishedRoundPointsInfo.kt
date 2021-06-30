package com.p2p.model.tuttifrutti

data class FinishedRoundPointsInfo (
    val player: String,
    val wordsPoints: List<Int>,
    var totalPoints: Int
)
