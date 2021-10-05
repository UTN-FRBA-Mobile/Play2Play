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
    JsonSubTypes.Type(value = Truco::class, name = "truco"),
    JsonSubTypes.Type(value = Retruco::class, name = "retruco"),
    JsonSubTypes.Type(value = ValeCuatro::class, name = "vale_cuatro"),
    JsonSubTypes.Type(value = Envido::class, name = "envido"),
    JsonSubTypes.Type(value = RealEnvido::class, name = "real_envido"),
    JsonSubTypes.Type(value = FaltaEnvido::class, name = "falta_envido"),
    JsonSubTypes.Type(value = EnvidoGoesFirst::class, name = "envido_goes_first"),
    JsonSubTypes.Type(value = YesIDo::class, name = "yes_i_do"),
    JsonSubTypes.Type(value = NoIDont::class, name = "no_i_dont"),
    JsonSubTypes.Type(value = GoToDeck::class, name = "go_to_deck"),
    JsonSubTypes.Type(value = CustomFinalActionResponse::class, name = "truco_custom"),
)
abstract class TrucoAction(
    val hasReplication: Boolean,
    val yesPoints: Int = 0,
    val noPoints: Int = 0,
    open val previousActions: List<TrucoAction> = emptyList()
) {

    abstract fun message(context: Context): String

    open fun availableResponses() = TrucoActionAvailableResponses()

    data class Truco(val round: Int, val envidoAlreadyAsked: Boolean = false) : TrucoAction(
        hasReplication = true,
        yesPoints = 2,
        noPoints = 1
    ), TrucoGameAction {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_truco)

        override fun availableResponses() =
            TrucoActionAvailableResponses(retruco = true, envidoGoesFirst = round == 1 && !envidoAlreadyAsked)

        override fun nextAction(): TrucoAction = Retruco
    }

    object Retruco : TrucoAction(
        hasReplication = true,
        yesPoints = 3,
        noPoints = 2
    ), TrucoGameAction {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_retruco)

        override fun availableResponses() = TrucoActionAvailableResponses(valeCuatro = true)
        override fun nextAction(): TrucoAction = ValeCuatro
    }

    object ValeCuatro : TrucoAction(
        hasReplication = true,
        yesPoints = 4,
        noPoints = 3
    ), TrucoGameAction {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_vale_cuatro)

        override fun nextAction(): TrucoAction? = null
    }

    data class Envido(
        override val previousActions: List<TrucoAction>
    ) : TrucoAction(
        hasReplication = true,
        yesPoints = (previousActions.lastOrNull()?.yesPoints ?: 0) + 2,
        noPoints = previousActions.lastOrNull()?.yesPoints ?: 1
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_envido)

        override fun availableResponses() = TrucoActionAvailableResponses(
            envido = previousActions.isEmpty(),
            realEnvido = true,
            faltaEnvido = true
        )
    }

    data class RealEnvido(override val previousActions: List<TrucoAction>) : TrucoAction(
        hasReplication = true,
        yesPoints = (previousActions.lastOrNull()?.yesPoints ?: 0) + 3 ,
        noPoints = previousActions.lastOrNull()?.yesPoints ?: 1
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_real_envido)

        override fun availableResponses() = TrucoActionAvailableResponses(faltaEnvido = true)
    }

    data class FaltaEnvido(val totalGamePoints: Int,
                           val totalOpponentPoints: Int,
                           override val previousActions: List<TrucoAction>) : TrucoAction(
        hasReplication = true,
        yesPoints = totalGamePoints - totalOpponentPoints,
        noPoints = previousActions.lastOrNull()?.yesPoints ?: 1
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_ask_for_falta_envido)
    }

    object EnvidoGoesFirst : TrucoAction(
        hasReplication = true,
        yesPoints = 2,
        noPoints = 1
    ) {
        override fun message(context: Context): String = context.getString(R.string.truco_ask_for_envido_goes_first)

        override fun availableResponses() = TrucoActionAvailableResponses(
            envido = true,
            realEnvido = true,
            faltaEnvido = true
        )

    }

    object YesIDo : TrucoAction(
        hasReplication = false
    ) {

        override fun message(context: Context) = context.getString(R.string.i_do)

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object NoIDont : TrucoAction(
        hasReplication = false
    ) {

        override fun message(context: Context) = context.getString(R.string.i_dont)

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    object GoToDeck : TrucoAction(
        hasReplication = false
    ) {

        override fun message(context: Context) = context.getString(R.string.truco_go_to_deck_action)

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }

    class CustomFinalActionResponse(
        val message: String,
        hasReplication: Boolean = false
    ) : TrucoAction(
        hasReplication = hasReplication
    ) {

        override fun message(context: Context) = message

        override fun availableResponses() =
            TrucoActionAvailableResponses(iDo = false, iDont = false)
    }
}