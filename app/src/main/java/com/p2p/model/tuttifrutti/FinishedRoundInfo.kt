package com.p2p.model.tuttifrutti

import android.os.Parcelable
import com.p2p.presentation.tuttifrutti.create.categories.Category
import kotlinx.parcelize.Parcelize

@Parcelize
data class FinishedRoundInfo(
    val player: String,
    val categoriesWords: LinkedHashMap<Category, String>,
    val saidEnough: Boolean = false
) : Parcelable
