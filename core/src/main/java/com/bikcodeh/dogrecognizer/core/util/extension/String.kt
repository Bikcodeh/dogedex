package com.bikcodeh.dogrecognizer.core.util.extension

fun String?.toSafeLong(): Long {
    return if (this.isNullOrEmpty()) -1L else this.toLong()
}