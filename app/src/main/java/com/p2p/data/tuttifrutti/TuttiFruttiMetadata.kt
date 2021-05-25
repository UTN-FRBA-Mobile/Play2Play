package com.p2p.data.tuttifrutti

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class TuttiFruttiMetadata(val totalRounds: Int, private val _categories: ArrayList<String>?): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(totalRounds)
        parcel.writeStringList(_categories)
    }

    override fun describeContents(): Int {
        return 0
    }

    val categories = _categories!!

    companion object CREATOR : Parcelable.Creator<TuttiFruttiMetadata> {
        override fun createFromParcel(parcel: Parcel): TuttiFruttiMetadata {
            return TuttiFruttiMetadata(parcel)
        }

        override fun newArray(size: Int): Array<TuttiFruttiMetadata?> {
            return arrayOfNulls(size)
        }
    }

}