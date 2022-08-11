package com.bikcodeh.dogrecognizer.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val id: Long,
    val index: Int,
    val name: String,
    val type: String,
    val heightFemale: String,
    val heightMale: String,
    var imageUrl: String,
    val lifeExpectancy: String,
    val temperament: String,
    val weightFemale: String,
    val weightMale: String
): Parcelable
