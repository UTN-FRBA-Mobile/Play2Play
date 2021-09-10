package com.p2p.presentation.truco.actions

import android.content.Context
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.p2p.R
import com.p2p.presentation.truco.actions.TrucoAction.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Trucazo::class, name = "Trucazo"),
    JsonSubTypes.Type(value = Retrucazo::class, name = "Retrucazo"),
    JsonSubTypes.Type(value = ValeCuatro::class, name = "ValeCuatro"),
    JsonSubTypes.Type(value = Envido::class, name = "Envido"),
    JsonSubTypes.Type(value = RealEnvido::class, name = "RealEnvido"),
    JsonSubTypes.Type(value = FaltaEnvido::class, name = "FaltaEnvido"),
    JsonSubTypes.Type(value = EnvidoGoesFirst::class, name = "EnvidoGoesFirst"),
    JsonSubTypes.Type(value = YesIDo::class, name = "YesIDo"),
    JsonSubTypes.Type(value = NoIDont::class, name = "NoIDont"),
    JsonSubTypes.Type(value = GoToDeck::class, name = "GoToDeck"),
    JsonSubTypes.Type(value = CustomFinalActionResponse::class, name = "CustomFinalActionResponse"),
)
abstract class TrucoAction(val hasReplication: Boolean, val points: Int) {

    abstract fun message(context: Context): String

    open fun availableResponses() = TrucoActionAvailableResponses()

    data class Trucazo(val round: Int, val envidoAlreadyAsked: Boolean = false) : TrucoAction(
        hasReplication = true,
        points = 1
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_truco)

        override fun availableResponses() =
            if (round == 1 && !envidoAlreadyAsked) TrucoActionAvailableResponses(retruco = true, envidoGoesFirst = true)
            else TrucoActionAvailableResponses(retruco = true)
    }

    object Retrucazo : TrucoAction(
        hasReplication = true,
        points = 1
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_retruco)

        override fun availableResponses() = TrucoActionAvailableResponses(valeCuatro = true)
    }

    object ValeCuatro : TrucoAction(
        hasReplication = true,
        points = 1
    ) {

        override fun message(context: Context) =
            context.getString(R.string.truco_ask_for_vale_cuatro)
    }

    data class Envido(val alreadyReplicatedEnvido: Boolean) : TrucoAction(
        hasReplication = true,
        points = 1
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_envido)

        override fun availableResponses() = TrucoActionAvailableResponses(
            envido = !alreadyReplicatedEnvido,
            realEnvido = true,
            faltaEnvido = true
        )
    }

    object RealEnvido : TrucoAction(
        hasReplication = true,
        points = 1
    ) {

        override fun message(context: Context) =
            context.getString(R.string.truco_ask_for_real_envido)

        override fun availableResponses() = TrucoActionAvailableResponses(faltaEnvido = true)
    }

    data class FaltaEnvido(val totalOpponentPoints: Int) : TrucoAction(
        hasReplication = true,
        points = 30 - totalOpponentPoints
    ) {

        override fun message(context: Context) =
            context.getString(R.string.truco_ask_for_falta_envido)
    }

    object EnvidoGoesFirst : TrucoAction(
        hasReplication = true,
        points = 1
    ) {
        override fun message(context: Context): String =
            context.getString(R.string.truco_ask_for_envido_goes_first)

    }

    object YesIDo : TrucoAction(
        hasReplication = false,
        points = 1
    ) {

        override fun message(context: Context) = context.getString(R.string.i_do)

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object NoIDont : TrucoAction(
        hasReplication = false,
        points = 0
    ) {

        override fun message(context: Context) = context.getString(R.string.i_dont)

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object GoToDeck : TrucoAction(
        hasReplication = false,
        points = 0
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_go_to_deck_action)

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    class CustomFinalActionResponse(
        val message: String,
        hasReplication: Boolean = false,
        points: Int
    ) : TrucoAction(
        hasReplication = hasReplication,
        points
    ) {

        override fun message(context: Context) = message

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }
}