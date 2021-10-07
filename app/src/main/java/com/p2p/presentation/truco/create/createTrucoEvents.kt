package com.p2p.presentation.truco.create

import com.p2p.presentation.tuttifrutti.create.categories.TuttiFruttiCategoriesEvents

sealed class CreateTrucoEvents

class GoToSelectPoints(val numberOfPlayers: Int) : CreateTrucoEvents()