package com.p2p.presentation.basegame

import com.p2p.model.base.message.CannotJoinToStartedGameMessage
import com.p2p.model.base.message.GoodbyePlayerMessage
import com.p2p.model.base.message.Message
import com.p2p.model.base.message.PauseGameMessage
import com.p2p.model.base.message.RejoinNameErrorMessage

enum class PlayersRecoverability {

    CANNOT_RECOVER {

        override fun constructOnPlayerLostMessage(lostPlayers: List<String>) = GoodbyePlayerMessage(lostPlayers.last())

        override fun constructCantJoinToStartedGameMessage(
            lostPlayers: List<String>
        ) = CannotJoinToStartedGameMessage()

        override fun shouldPauseGame(lostPlayers: List<String>) = false

        override fun shouldResumeGame(lostPlayers: List<String>) = false

        override fun canJoinToStartedGame(
            lostPlayers: List<String>,
            newPlayerName: String
        ) = false
    },

    MUST_BE_RECOVERED {

        override fun constructOnPlayerLostMessage(lostPlayers: List<String>) = PauseGameMessage(lostPlayers)

        override fun constructCantJoinToStartedGameMessage(
            lostPlayers: List<String>
        ) = RejoinNameErrorMessage(lostPlayers)

        override fun shouldPauseGame(lostPlayers: List<String>) = lostPlayers.isNotEmpty()

        override fun shouldResumeGame(lostPlayers: List<String>) = lostPlayers.isEmpty()

        override fun canJoinToStartedGame(
            lostPlayers: List<String>,
            newPlayerName: String
        ) = newPlayerName in lostPlayers
    };

    abstract fun constructOnPlayerLostMessage(lostPlayers: List<String>): Message
    abstract fun constructCantJoinToStartedGameMessage(lostPlayers: List<String>): Message
    abstract fun shouldPauseGame(lostPlayers: List<String>): Boolean
    abstract fun shouldResumeGame(lostPlayers: List<String>): Boolean
    abstract fun canJoinToStartedGame(lostPlayers: List<String>, newPlayerName: String): Boolean
}
