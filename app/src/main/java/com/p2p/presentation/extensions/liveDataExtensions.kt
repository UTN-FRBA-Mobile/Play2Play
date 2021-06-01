package com.p2p.presentation.extensions

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.requireValue(lazyMessage: () -> String = { "Value of livedata was required to be not null" }): T {
    return requireNotNull(value, lazyMessage)
}
