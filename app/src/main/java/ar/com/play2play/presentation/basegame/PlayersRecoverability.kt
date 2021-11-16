package ar.com.play2play.presentation.basegame

import ar.com.play2play.model.base.message.CannotJoinToStartedGameMessage
import ar.com.play2play.model.base.message.GoodbyePlayerMessage
import ar.com.play2play.model.base.message.Message
import ar.com.play2play.model.base.message.PauseGameMessage
import ar.com.play2play.model.base.message.RejoinNameErrorMessage

enum class PlayersRecoverability {

    CANNOT_RECOVER {

        override fun constructOnPlayerLostMessage(lostPlayers: Set<String>) = GoodbyePlayerMessage(lostPlayers.last())

        override fun constructCantJoinToStartedGameMessage(
            lostPlayers: Set<String>
        ) = CannotJoinToStartedGameMessage()

        override fun shouldPauseGame(lostPlayers: Set<String>) = false

        override fun shouldResumeGame(lostPlayers: Set<String>) = false

        override fun canJoinToStartedGame(
            lostPlayers: Set<String>,
            newPlayerName: String
        ) = false
    },

    MUST_BE_RECOVERED {

        override fun constructOnPlayerLostMessage(lostPlayers: Set<String>) =
            PauseGameMessage(lostPlayers.toList())

        override fun constructCantJoinToStartedGameMessage(
            lostPlayers: Set<String>
        ) = RejoinNameErrorMessage(lostPlayers.toList())

        override fun shouldPauseGame(lostPlayers: Set<String>) = lostPlayers.isNotEmpty()

        override fun shouldResumeGame(lostPlayers: Set<String>) = lostPlayers.isEmpty()

        override fun canJoinToStartedGame(
            lostPlayers: Set<String>,
            newPlayerName: String
        ) = newPlayerName in lostPlayers
    };

    abstract fun constructOnPlayerLostMessage(lostPlayers: Set<String>): Message
    abstract fun constructCantJoinToStartedGameMessage(lostPlayers: Set<String>): Message
    abstract fun shouldPauseGame(lostPlayers: Set<String>): Boolean
    abstract fun shouldResumeGame(lostPlayers: Set<String>): Boolean
    abstract fun canJoinToStartedGame(lostPlayers: Set<String>, newPlayerName: String): Boolean
}
