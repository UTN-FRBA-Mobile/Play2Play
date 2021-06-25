package com.p2p.presentation.tuttifrutti.review

import com.p2p.model.tuttifrutti.FinishedRoundPointsInfo

sealed class TuttiFruttiReviewEvents

class FinishRoundReview(val finishedRoundPointsInfo: List<FinishedRoundPointsInfo>) : TuttiFruttiReviewEvents()
