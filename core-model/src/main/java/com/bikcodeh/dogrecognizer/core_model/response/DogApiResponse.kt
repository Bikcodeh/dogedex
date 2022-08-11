package com.bikcodeh.dogrecognizer.core_model.response

import com.squareup.moshi.Json

data class DogApiResponse(
    val message: String,
    @Json(name = "is_success")
    val isSuccess: Boolean,
    val data: DogResponse
)