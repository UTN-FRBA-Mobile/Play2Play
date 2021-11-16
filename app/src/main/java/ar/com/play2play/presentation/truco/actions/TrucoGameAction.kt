package ar.com.play2play.presentation.truco.actions

interface TrucoGameAction {
    fun nextAction(): TrucoAction?
}