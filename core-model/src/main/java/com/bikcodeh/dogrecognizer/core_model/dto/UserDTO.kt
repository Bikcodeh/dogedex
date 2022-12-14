package com.bikcodeh.dogrecognizer.core_model.dto

import com.bikcodeh.dogrecognizer.core_model.User
import com.squareup.moshi.Json

class UserDTO(
    val id: Long,
    val email: String,
    @field:Json(name = "authentication_token")
    val authenticationToken: String
) {
    fun toDomain(): User = User(id, email, authenticationToken)
}