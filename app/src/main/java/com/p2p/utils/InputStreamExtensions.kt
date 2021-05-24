package com.p2p.presentation.extensions

import java.io.BufferedReader
import java.io.InputStream

fun InputStream.getString(): String{
    val reader = BufferedReader(this.reader())
    var content: String
    try {
        content = reader.readText()
    } finally {
        reader.close()
    }
    return content
}