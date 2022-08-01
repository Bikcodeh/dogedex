package com.bikcodeh.dogrecognizer.domain.repository

import com.bikcodeh.dogrecognizer.data.remote.dto.DefaultResponse
import com.bikcodeh.dogrecognizer.domain.model.Dog
import com.bikcodeh.dogrecognizer.domain.common.Result
import retrofit2.Response

interface DogRepository {
    suspend fun downloadDogs(): Result<List<Dog>>
    suspend fun addDogToUser(dogId: String): Result<DefaultResponse>
}