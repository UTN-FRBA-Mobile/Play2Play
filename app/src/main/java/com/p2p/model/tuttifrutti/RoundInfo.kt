package com.p2p.model.tuttifrutti

import com.p2p.presentation.tuttifrutti.create.categories.Category

data class RoundInfo(val letter: Char, val values: Map<Category, String>)
