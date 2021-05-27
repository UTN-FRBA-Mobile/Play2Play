package com.p2p.model.tuttifrutti

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TuttiFruttiInfo(val totalRounds: Int, private val categories: List<String>): Parcelable
