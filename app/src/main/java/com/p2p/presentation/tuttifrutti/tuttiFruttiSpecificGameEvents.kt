package com.p2p.presentation.tuttifrutti

import com.p2p.presentation.basegame.SpecificGameEvent
import com.p2p.presentation.tuttifrutti.create.categories.Category

sealed class TuttiFruttiSpecificGameEvent : SpecificGameEvent()

object InvalidInputs : TuttiFruttiSpecificGameEvent()

class FinishRound(val categoriesWithValues: Map<Category, String>): TuttiFruttiSpecificGameEvent()

object GoToReview : TuttiFruttiSpecificGameEvent()
