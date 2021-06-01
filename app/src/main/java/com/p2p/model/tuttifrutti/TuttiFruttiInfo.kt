package com.p2p.model.tuttifrutti

import android.os.Parcelable
import com.p2p.model.GameInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class TuttiFruttiInfo(val totalRounds: Int, val categories: List<String>): Parcelable, GameInfo
