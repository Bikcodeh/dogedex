package com.bikcodeh.dogrecognizer.core.util.extension

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bikcodeh.dogrecognizer.core.R
import com.bikcodeh.dogrecognizer.core.util.Constants

fun Context.getStringOrNull(@StringRes resId: Int?): String? {
    return try {
        resId?.let { getString(it) }
    } catch (e: Exception) {
        null
    }
}

fun Context.getSafeString(@StringRes resId: Int?): String {
    return resId?.let { getString(it) }
        ?: run {
            getString(R.string.error_unknown)
        }
}

fun Context.createProgressDialog(): AlertDialog {
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
    return AlertDialog.Builder(this)
        .setCancelable(false)
        .setView(view)
        .create()
}