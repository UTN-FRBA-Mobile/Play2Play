package com.p2p.presentation.truco

import androidx.annotation.ColorRes
import com.p2p.R

enum class TrucoRoundResult(@ColorRes val color: Int) {
    WIN(R.color.colorSuccess),
    TIE(R.color.colorWarning),
    DEFEAT(R.color.colorError)
}
