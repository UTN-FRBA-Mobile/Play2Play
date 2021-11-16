package ar.com.play2play.presentation.truco

import ar.com.play2play.model.truco.TeamPlayer

enum class TrucoPlayerPosition {
    FRONT,
    LEFT,
    RIGHT,
    MY_SELF;

    companion object {

        fun get(player: TeamPlayer, players: List<TeamPlayer>, mySelf: TeamPlayer) = when (players.size) {
            2 -> if (player == mySelf) MY_SELF else FRONT
            4 -> when (getPlayersWithMyselfFirst(players, mySelf).indexOf(player)) {
                0 -> MY_SELF
                1 -> RIGHT
                2 -> FRONT
                3 -> LEFT
                else -> throw IllegalStateException("Invalid position on truco for 4")
            }
            else -> throw IllegalStateException("Truco for ${players.size} not implemented yet.")
        }

        private tailrec fun getPlayersWithMyselfFirst(players: List<TeamPlayer>, mySelf: TeamPlayer): List<TeamPlayer> {
            return if (players.first() == mySelf) {
                players
            } else {
                getPlayersWithMyselfFirst(players.drop(1) + players.take(1), mySelf)
            }
        }
    }
}
