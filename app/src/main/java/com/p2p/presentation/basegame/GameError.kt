package com.p2p.presentation.basegame

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.p2p.R

abstract class GameError(@DrawableRes val image: Int, @StringRes val text: Int, val onRetry: () -> Unit)

class CannotEstablishClientConnectionError(onRetry: () -> Unit) : GameError(
    R.drawable.ic_no_cell,
    R.string.error_on_client_connection,
    onRetry
)
