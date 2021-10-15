package com.p2p.model.truco

data class FinalResult(
    val isWinner: Boolean,
    val ourScore: Int,
    val theirScore: Int
)