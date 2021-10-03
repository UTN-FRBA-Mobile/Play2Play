package com.p2p.presentation.home.games

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.p2p.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Game(
    @StringRes val nameRes: Int, 
    @DrawableRes val iconRes: Int,
    @RawRes val instructionsRes: Int
) : Parcelable {

    TUTTI_FRUTTI(
        R.string.games_tutti_frutti,
        R.drawable.ic_tutti_frutti,
        R.raw.tutti_frutti_instructions
    ),
    TRUCO(
        R.string.games_truco,
        R.drawable.ic_truco,
        R.raw.truco_instructions
    ),
    IMPOSTOR(
        R.string.games_impostor,
        R.drawable.ic_emulate_impostor,
        R.raw.impostor_instructions
    ),
}
