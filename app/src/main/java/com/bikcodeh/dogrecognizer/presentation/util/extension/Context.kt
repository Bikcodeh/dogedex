package com.bikcodeh.dogrecognizer.presentation.util.extension

import android.content.Context
import androidx.annotation.StringRes

fun Context.getStringOrNull(@StringRes resId: Int?): String? {
    return try {
        resId?.let { getString(it) }
    } catch (e: Exception) {
        null
    }
}