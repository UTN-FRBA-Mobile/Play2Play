package com.p2p.presentation.tuttifrutti

import com.p2p.presentation.base.game.AbstractGameCreationEvent

sealed class TuttiFruttiCreationEvents : AbstractGameCreationEvent

object GoToSelectCategories : TuttiFruttiCreationEvents()