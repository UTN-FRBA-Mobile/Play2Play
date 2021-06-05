package com.p2p.presentation.tuttifrutti.play

import com.p2p.presentation.tuttifrutti.create.categories.Category

sealed class TuttiFruttiPlayingEvents

object InvalidInputs : TuttiFruttiPlayingEvents()

class FinishRound(val categoriesWithValues: Map<Category, String>): TuttiFruttiPlayingEvents()