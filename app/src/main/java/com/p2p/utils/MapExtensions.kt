package com.p2p.utils

fun <T, N> MutableMap<T, N?>.nonNullable(): Map<T, N> = this.map { it.key to it.value!! }.toMap()
