package com.p2p.presentation.home.games

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.p2p.R

enum class Game(@StringRes val nameRes: Int, @DrawableRes val imageRes: Int) {
    TUTTI_FRUTTI(R.string.games_tutti_frutti, R.drawable.tutti_frutti),
}
