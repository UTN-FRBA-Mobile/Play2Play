package com.p2p.data.tuttifrutti

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

@Parcelize
data class TuttiFruttiMetadata(val totalRounds: Int, private val categories: List<String>): Parcelable
