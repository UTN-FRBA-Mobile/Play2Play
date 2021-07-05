package com.p2p.presentation.tuttifrutti

import com.p2p.R
import com.p2p.presentation.basegame.GameError

class SinglePlayerOnGame(onActionClicked: () -> Unit) : GameError(
    R.drawable.ic_crown,
    R.string.tf_error_single_player,
    R.string.thanks,
    onActionClicked
)
