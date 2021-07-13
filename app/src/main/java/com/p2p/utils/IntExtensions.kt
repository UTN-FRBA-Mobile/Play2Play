package com.p2p.utils

fun Int.isEven() = this % 2 == 0

fun Int.toBoolean() = this != 0

fun Int.toByteArray() = if (this <= Byte.MAX_VALUE) ByteArray(1) { toByte() } else null
