package com.p2p.utils

import java.io.InputStream

fun InputStream.getString() = this.bufferedReader().use { it.readText() }
