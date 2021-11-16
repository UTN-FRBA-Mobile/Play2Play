package ar.com.play2play.model.tuttifrutti

import android.os.Parcelable
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinishedRoundInfo(
    val peer: Long,
    val player: String,
    val categoriesWords: LinkedHashMap<Category, String>,
    val saidEnough: Boolean = false
) : Parcelable
