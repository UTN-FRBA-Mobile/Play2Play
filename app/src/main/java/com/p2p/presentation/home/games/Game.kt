package com.p2p.presentation.home.games

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.p2p.R

enum class Game(@StringRes val nameRes: Int, @DrawableRes val iconRes: Int,
                @RawRes val instructionsRes: Int) {

    TUTTI_FRUTTI(R.string.games_tutti_frutti, R.drawable.ic_tutti_frutti, R.raw.tutti_frutti_instructions),
}
