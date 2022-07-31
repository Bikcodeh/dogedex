package com.bikcodeh.dogrecognizer.presentation.util.extension

fun String?.toSafeLong(): Long {
    return if (this.isNullOrEmpty()) -1L else this.toLong()
}