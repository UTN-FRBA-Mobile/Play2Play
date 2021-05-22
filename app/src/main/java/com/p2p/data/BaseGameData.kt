package com.p2p.data

import androidx.annotation.RawRes
import androidx.annotation.StringRes

/** Common info for a game
 * Hint: @StringRes can be called then with context?.getText(text)
 * */
data class BaseGameData(@StringRes val name: Int, @RawRes val instructions: Int)