package com.bikcodeh.dogrecognizer.core.util.extension

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun Fragment.observeFlows(crossinline observationFunction: suspend (CoroutineScope) -> Unit) {
    viewLifecycleOwner.lifecycle.coroutineScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            observationFunction(this)
        }
    }
}

fun Fragment.launchSafeActivity(pathClassName: String, clear: Boolean = false) {
    try {
        Intent(activity, Class.forName(pathClassName)).apply {
            if (clear) {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}