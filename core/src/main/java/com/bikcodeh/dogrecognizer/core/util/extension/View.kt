package com.bikcodeh.dogrecognizer.core.util.extension

import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
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

fun View.initAnimation(@IdRes animationRes: Int) {
    this.startAnimation(AnimationUtils.loadAnimation(context, animationRes))
}

