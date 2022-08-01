package com.bikcodeh.dogrecognizer.presentation.util.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snack(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, length).show()
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

