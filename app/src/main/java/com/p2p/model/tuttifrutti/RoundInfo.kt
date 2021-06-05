package com.p2p.model.tuttifrutti

import com.p2p.presentation.tuttifrutti.create.categories.Category

open class RoundInfo(val letter: Char, val number: Int) {
    fun finish(values: Map<Category, String>) = FinishedRoundInfo(letter, number, values)
}

class FinishedRoundInfo(letter: Char, round: Int, val values: Map<Category, String>) :
    RoundInfo(letter, round)
