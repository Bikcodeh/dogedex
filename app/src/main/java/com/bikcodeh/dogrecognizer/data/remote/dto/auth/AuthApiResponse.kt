package com.bikcodeh.dogrecognizer.data.remote.dto.auth

import com.squareup.moshi.Json

class AuthApiResponse(
    val message: String,
    @Json(name = "is_success")
    val isSuccess: Boolean,
    val data: UserResponse
)