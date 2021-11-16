package ar.com.play2play.presentation.home.games

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import ar.com.play2play.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Game(
    @StringRes val nameRes: Int, 
    @DrawableRes val iconRes: Int,
    @RawRes val instructionsRes: Int,
    val id: String
) : Parcelable {

    TUTTI_FRUTTI(
        R.string.games_tutti_frutti,
        R.drawable.ic_tutti_frutti,
        R.raw.tutti_frutti_instructions,
        "TuttiFrutti"
    ),
    TRUCO(
        R.string.games_truco,
        R.drawable.ic_truco,
        R.raw.truco_instructions,
        "Truco"
    ),
    IMPOSTOR(
        R.string.games_impostor,
        R.drawable.ic_impostor,
        R.raw.impostor_instructions,
        "Impostor"
    ),
}
