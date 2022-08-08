package com.bikcodeh.dogrecognizer.core.remote.dto.doglist

import com.bikcodeh.dogrecognizer.core.model.Dog
import com.squareup.moshi.Json

data class DogDTO(
    val id: Long,
    val index: Int,
    @field:Json(name = "name_en")
    val name: String,
    @field:Json(name = "dog_type")
    val type: String,
    @field:Json(name = "height_female")
    val heightFemale: String,
    @field:Json(name = "height_male")
    val heightMale: String,
    @field:Json(name = "image_url")
    val imageUrl: String,
    @field:Json(name = "life_expectancy")
    val lifeExpectancy: String,
    val temperament: String,
    @field:Json(name = "weight_female")
    val weightFemale: String,
    @field:Json(name = "weight_male")
    val weightMale: String
) {
    fun toDomain(): Dog {
        return Dog(
            id,
            index,
            name,
            type,
            heightFemale,
            heightMale,
            imageUrl,
            lifeExpectancy,
            temperament,
            weightFemale,
            weightMale
        )
    }
}
