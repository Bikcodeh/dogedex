package com.bikcodeh.dogrecognizer.core_model.dto

import com.squareup.moshi.Json

data class AddDogToUserDTO(
    @field:Json(name = "dog_id")
    val dogId: String
)
