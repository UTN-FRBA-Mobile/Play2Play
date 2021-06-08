package com.p2p.utils

fun Boolean.toInt() = if (this) 1 else 0

fun Boolean.toByte() = toInt().toByte()

fun Boolean.toByteArray() = ByteArray(1) { toByte() }
