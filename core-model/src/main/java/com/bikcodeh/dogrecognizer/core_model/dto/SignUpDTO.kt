package com.bikcodeh.dogrecognizer.core_model.dto

import com.squareup.moshi.Json

data class SignUpDTO(
    val email: String,
    val password: String,
    @field:Json(name = "password_confirmation")
    val confirmPassword: String
)
