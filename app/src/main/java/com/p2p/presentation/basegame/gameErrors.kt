package com.p2p.presentation.basegame

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.p2p.R

abstract class GameError(
    @DrawableRes val image: Int,
    @StringRes val text: Int,
    @StringRes val actionText: Int,
    val onActionClicked: () -> Unit,
    val stringArgs: List<Any> = emptyList()
)

class CannotEstablishClientConnectionError(onRetry: () -> Unit) : GameError(
    R.drawable.ic_no_cell,
    R.string.error_client_connection_failure,
    R.string.error_screen_retry_button,
    onRetry
)

class NameInUseError(onOkClicked: () -> Unit) : GameError(
    R.drawable.ic_info,
    R.string.error_name_in_use,
    android.R.string.ok,
    onOkClicked
)

class RoomIsAlreadyFullError(onOkClicked: () -> Unit) : GameError(
    R.drawable.ic_info,
    R.string.error_room_already_full,
    android.R.string.ok,
    onOkClicked
)

class RejoinNameError(availableNames: List<String>, onOkClicked: () -> Unit) : GameError(
    R.drawable.ic_info,
    R.string.error_rejoin_name,
    android.R.string.ok,
    onOkClicked,
    listOf(availableNames.joinToString(".\n- ", "\n- ", "."))
)
    
class WrongJoinedGameError(onOkClicked: () -> Unit) : GameError(
    R.drawable.ic_info,
    R.string.error_joined_game,
    android.R.string.ok,
    onOkClicked
)

class ServerConnectionLostError(onOkClicked: () -> Unit) : GameError(
    R.drawable.ic_no_cell,
    R.string.error_server_connection_failure,
    android.R.string.ok,
    onOkClicked
)
