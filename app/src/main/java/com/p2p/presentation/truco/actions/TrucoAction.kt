package com.p2p.presentation.truco.actions

import android.content.Context
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.p2p.R
import com.p2p.model.base.message.ClientHandshakeMessage
import com.p2p.model.base.message.GoodbyePlayerMessage
import com.p2p.model.base.message.NameInUseMessage
import com.p2p.model.base.message.ServerHandshakeMessage
import com.p2p.model.impostor.message.ImpostorAssignWord
import com.p2p.model.impostor.message.ImpostorEndGame
import com.p2p.model.truco.message.TrucoActionMessage
import com.p2p.model.truco.message.TrucoCardsMessage
import com.p2p.model.tuttifrutti.message.*
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
    JsonSubTypes.Type(value = ValeCuatro::class, name = "ValeCuatro"),
    JsonSubTypes.Type(value = FaltaEnvido::class, name = "FaltaEnvido"),
    JsonSubTypes.Type(value = YesIDo::class, name = "YesIDo"),
    JsonSubTypes.Type(value = NoIDont::class, name = "NoIDont"),
    JsonSubTypes.Type(value = GoToDeck::class, name = "GoToDeck"),
    JsonSubTypes.Type(value = CustomFinalActionResponse::class, name = "CustomFinalActionResponse"),
)
abstract class TrucoAction(val hasReplication: Boolean) {

    abstract fun message(context: Context): String

    open fun availableResponses() = TrucoActionAvailableResponses()

    object Trucazo : TrucoAction(
        hasReplication = true
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_truco)

        override fun availableResponses() = TrucoActionAvailableResponses(retruco = true)
    }

    object Retrucazo : TrucoAction(
        hasReplication = true
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_retruco)

        override fun availableResponses() = TrucoActionAvailableResponses(valeCuatro = true)
    }

    object ValeCuatro : TrucoAction(
        hasReplication = true
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_vale_cuatro)
    }

    class Envido(val alreadyReplicatedEnvido: Boolean) : TrucoAction(
        hasReplication = true
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_envido)

        override fun availableResponses() = TrucoActionAvailableResponses(
            envido = !alreadyReplicatedEnvido,
            realEnvido = true,
            faltaEnvido = true
        )
    }

    object RealEnvido : TrucoAction(
        hasReplication = true
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_real_envido)

        override fun availableResponses() = TrucoActionAvailableResponses(faltaEnvido = true)
    }

    object FaltaEnvido : TrucoAction(
        hasReplication = true
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_falta_envido)
    }

    object YesIDo : TrucoAction(
        hasReplication = false
    ) {

        override fun message(context: Context) = context.getString(R.string.i_do)

        override fun availableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object NoIDont : TrucoAction(
        hasReplication = false
    ) {

        override fun message(context: Context) = context.getString(R.string.i_dont)

        override fun availableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object GoToDeck : TrucoAction(
        hasReplication = false
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_go_to_deck_action)

        override fun availableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    class CustomFinalActionResponse(
        private val message: String,
        hasReplication: Boolean = false
    ) : TrucoAction(
        hasReplication = hasReplication
    ) {

        override fun message(context: Context) = message

        override fun availableResponses() = TrucoActionAvailableResponses(iDo = false, iDont = false)
    }
}