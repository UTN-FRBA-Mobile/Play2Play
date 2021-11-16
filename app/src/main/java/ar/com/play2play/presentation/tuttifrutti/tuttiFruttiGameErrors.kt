package ar.com.play2play.presentation.tuttifrutti

import ar.com.play2play.R
import ar.com.play2play.presentation.basegame.GameError

class SinglePlayerOnGame(onActionClicked: () -> Unit) : GameError(
    R.drawable.ic_crown,
    R.string.tf_error_single_player,
    R.string.thanks,
    onActionClicked
)
