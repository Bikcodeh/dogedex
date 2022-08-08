package com.bikcodeh.dogrecognizer.core.remote.dto.auth

import com.bikcodeh.dogrecognizer.core.model.User
import com.squareup.moshi.Json

class UserDTO(
    val id: Long,
    val email: String,
    @field:Json(name = "authentication_token")
    val authenticationToken: String
) {
    fun toDomain(): User = User(id, email, authenticationToken)
}