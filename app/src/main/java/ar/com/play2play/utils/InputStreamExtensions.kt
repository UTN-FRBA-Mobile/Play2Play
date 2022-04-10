package ar.com.play2play.utils

import java.io.InputStream

fun InputStream.getString() = this.bufferedReader().use { it.readText() }
