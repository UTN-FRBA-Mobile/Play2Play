package com.p2p.presentation.truco.actions

import android.content.Context
import com.p2p.R

abstract class TrucoAction(val hasReplication: Boolean) {

    abstract fun getMessage(context: Context): String

    open fun getAvailableResponses() = TrucoActionAvailableResponses()

    object Truco : TrucoAction(
        hasReplication = true
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.truco_ask_for_truco)

        override fun getAvailableResponses() = TrucoActionAvailableResponses(retruco = true)
    }

    object Retruco : TrucoAction(
        hasReplication = true
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.truco_ask_for_retruco)

        override fun getAvailableResponses() = TrucoActionAvailableResponses(valeCuatro = true)
    }

    object ValeCuatro : TrucoAction(
        hasReplication = true
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.truco_ask_for_vale_cuatro)
    }

    class Envido(private val alreadyReplicatedEnvido: Boolean) : TrucoAction(
        hasReplication = true
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.truco_ask_for_envido)

        override fun getAvailableResponses() = TrucoActionAvailableResponses(
            envido = !alreadyReplicatedEnvido,
            realEnvido = true,
            faltaEnvido = true
        )
    }

    object RealEnvido : TrucoAction(
        hasReplication = true
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.truco_ask_for_real_envido)

        override fun getAvailableResponses() = TrucoActionAvailableResponses(faltaEnvido = true)
    }

    object FaltaEnvido : TrucoAction(
        hasReplication = true
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.truco_ask_for_falta_envido)
    }

    object YesIDo : TrucoAction(
        hasReplication = false
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.i_do)

        override fun getAvailableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object NoIDont : TrucoAction(
        hasReplication = false
    ) {

        override fun getMessage(context: Context) = context.getString(R.string.i_dont)

        override fun getAvailableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    class CustomFinalActionResponse(
        private val message: String,
        hasReplication: Boolean = false
    ) : TrucoAction(
        hasReplication = hasReplication
    ) {

        override fun getMessage(context: Context) = message

        override fun getAvailableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }
}