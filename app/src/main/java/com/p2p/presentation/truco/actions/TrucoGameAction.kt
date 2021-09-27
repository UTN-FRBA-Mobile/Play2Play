package com.p2p.presentation.truco.actions

interface TrucoGameAction {
    fun nextAction(): TrucoAction?
}