package com.p2p.presentation.tuttifrutti.create

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.p2p.R

enum class Category(@StringRes val nameRes: Int) {
    COUNTRIES(R.string.tf_categories_countries),
    NAMES(R.string.tf_categories_names),
    ANIMALS(R.string.tf_categories_animals),
    FOODS(R.string.tf_categories_foods),
    MOVIES(R.string.tf_categories_movies),
    COLOURS(R.string.tf_categories_colours)
}
