package com.bikcodeh.dogrecognizer.core.remote.dto

import com.squareup.moshi.Json

data class DogApiResponse(
    val message: String,
    @Json(name = "is_success")
    val isSuccess: Boolean,
    val data: DogResponse
)