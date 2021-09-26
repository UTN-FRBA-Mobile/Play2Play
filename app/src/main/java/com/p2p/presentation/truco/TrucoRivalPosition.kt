package com.p2p.presentation.truco

import com.p2p.model.truco.PlayerTeam

enum class TrucoRivalPosition {
    FRONT,
    LEFT,
    RIGHT,
    MY_SELF;

    companion object {

        fun get(player: PlayerTeam, players: List<PlayerTeam>, mySelf: PlayerTeam) = when (players.size) {
            2 -> if (player == mySelf) MY_SELF else FRONT
            4 -> when (getPlayersWithMyselfFirst(players, mySelf).indexOf(player)) {
                0 -> MY_SELF
                1 -> LEFT
                2 -> FRONT
                3 -> RIGHT
                else -> throw IllegalStateException("Invalid position on truco for 4")
            }
            else -> throw IllegalStateException("Truco for ${players.size} not implemented yet.")
        }

        private tailrec fun getPlayersWithMyselfFirst(players: List<PlayerTeam>, mySelf: PlayerTeam): List<PlayerTeam> {
            return if (players.first() == mySelf) {
                players
            } else {
                getPlayersWithMyselfFirst(players.drop(1) + players.take(1), mySelf)
            }
        }
    }
}