package com.p2p.presentation.truco

import androidx.annotation.ColorRes
import com.p2p.R
import com.p2p.model.truco.TeamPlayer

enum class TrucoRoundResult(@ColorRes val color: Int) {
    WIN(R.color.colorSuccess),
    TIE(R.color.colorWarning),
    DEFEAT(R.color.colorError);

    companion object {

        fun get(winner: TeamPlayer?, mySelf: TeamPlayer) = when (winner?.team) {
            null -> TIE
            mySelf.team -> WIN
            else -> DEFEAT
        }
    }
}
