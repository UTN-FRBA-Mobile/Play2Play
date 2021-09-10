package com.p2p.presentation.truco.actions

data class TrucoActionAvailableResponses(
    val iDo: Boolean = true,
    val iDont: Boolean = true,
    val envido: Boolean = false,
    val realEnvido: Boolean = false,
    val faltaEnvido: Boolean = false,
    val retruco: Boolean = false,
    val valeCuatro: Boolean = false,
    val envidoGoesFirst: Boolean = false
) {

    fun hasAvailableResponses() = listOf(iDo, iDont, envido, realEnvido, faltaEnvido, retruco, valeCuatro).any { it }

    companion object {

        fun noActions() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }
}
