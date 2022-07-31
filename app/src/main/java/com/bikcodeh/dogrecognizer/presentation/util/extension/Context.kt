package com.bikcodeh.dogrecognizer.presentation.util.extension

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.bikcodeh.dogrecognizer.R

fun Context.getStringOrNull(@StringRes resId: Int?): String? {
    return try {
        resId?.let { getString(it) }
    } catch (e: Exception) {
        null
    }
}

fun Context.createProgressDialog(): AlertDialog {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
    return AlertDialog.Builder(this)
        .setCancelable(false)
        .setView(view)
        .create()
}