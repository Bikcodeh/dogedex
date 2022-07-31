package com.bikcodeh.dogrecognizer.data.remote.dto.user

import com.squareup.moshi.Json

data class AddDogToUserDTO(
    @field:Json(name = "dog_id")
    val dogId: String
)
