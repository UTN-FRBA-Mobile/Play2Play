package ar.com.play2play.presentation.tuttifrutti.review

import ar.com.play2play.model.tuttifrutti.FinishedRoundPointsInfo

sealed class TuttiFruttiReviewEvents

class FinishRoundReview(val finishedRoundPointsInfo: List<FinishedRoundPointsInfo>) : TuttiFruttiReviewEvents()
