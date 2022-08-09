package com.bikcodeh.dogrecognizer.core.util.extension

import android.app.Activity
import android.content.Intent

fun Activity.launchSafeActivity(pathClassName: String) {
    try {
        Intent(this, Class.forName(pathClassName)).apply {
            startActivity(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}