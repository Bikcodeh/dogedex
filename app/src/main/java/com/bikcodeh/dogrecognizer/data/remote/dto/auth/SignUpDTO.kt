package com.bikcodeh.dogrecognizer.data.remote.dto.auth

import com.squareup.moshi.Json

data class SignUpDTO(
    val email: String,
    val password: String,
    @field:Json(name = "password_confirmation")
    val confirmPassword: String
)
