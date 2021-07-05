package com.p2p.utils

import androidx.core.text.HtmlCompat

fun String.fromHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
