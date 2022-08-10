package com.bikcodeh.dogrecognizer.core.util.extension

import java.net.URLDecoder
import java.net.URLEncoder

fun String?.toSafeLong(): Long {
    return if (this.isNullOrEmpty()) -1L else this.toLong()
}

fun String.encode(charset: String = "UTF-8"): String {
    return URLEncoder.encode(this, charset)
}

fun String.decode(charset: String = "UTF-8"): String {
    return URLDecoder.decode(this, charset)
}