package com.p2p.presentation.basegame

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.p2p.R

abstract class GameError(
    @DrawableRes val image: Int,
    @StringRes val text: Int,
    @StringRes val actionText: Int,
    val onActionClicked: () -> Unit
)

class CannotEstablishClientConnectionError(onRetry: () -> Unit) : GameError(
    R.drawable.ic_no_cell,
    R.string.error_client_connection_failure,
    R.string.error_screen_retry_button,
    onRetry
)
