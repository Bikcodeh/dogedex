package com.bikcodeh.dogrecognizer.core.util

import androidx.navigation.NavOptions
import com.bikcodeh.dogrecognizer.core.R

object Util {

    fun setDefaultTransitionAnimation(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.from_right)
            .setExitAnim(R.anim.to_left)
            .setPopEnterAnim(R.anim.from_left)
            .setPopExitAnim(R.anim.to_right)
            .build()
    }
}