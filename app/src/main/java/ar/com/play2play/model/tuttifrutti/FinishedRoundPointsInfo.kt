package ar.com.play2play.model.tuttifrutti

data class FinishedRoundPointsInfo (
    val player: String,
    val wordsPoints: List<Int>,
    var totalPoints: Int
)
